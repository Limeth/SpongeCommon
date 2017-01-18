/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.mixin.core.item.recipe.smelting;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.smelting.SmeltingRecipe;
import org.spongepowered.api.item.recipe.smelting.SmeltingRecipeRegistry;
import org.spongepowered.api.item.recipe.smelting.SmeltingResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.common.item.inventory.util.ItemStackUtil;
import org.spongepowered.common.item.recipe.smelting.MatchSmeltingVanillaItemStack;
import org.spongepowered.common.item.recipe.smelting.SpongeSmeltingRecipe;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

@Mixin(FurnaceRecipes.class)
public abstract class MixinFurnaceRecipes implements SmeltingRecipeRegistry {

    @Shadow @Final private Map<ItemStack, ItemStack> smeltingList;
    @Shadow @Final private Map<ItemStack, Float> experienceList;

    private final List<SmeltingRecipe> customRecipes = Lists.newArrayList();
    // No IdentityHashBiMap implementation exists
    private final Map<SmeltingRecipe, ItemStack> customRecipeToNativeIngredient = new IdentityHashMap<>();
    private final Map<ItemStack, SmeltingRecipe> nativeIngredientToCustomRecipe = new IdentityHashMap<>();

    @Shadow public abstract ItemStack getSmeltingResult(ItemStack stack);
    @Shadow public abstract float getSmeltingExperience(ItemStack stack);

    @Inject(method = "getSmeltingResult", at = @At("RETURN"), cancellable = true)
    public void onGetSmeltingResult(ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        ItemStackSnapshot ingredient = ItemStackUtil.snapshotOf(stack);
        Optional<SmeltingResult> result = getCustomResult(ingredient);

        if (result.isPresent()) {
            ItemStack nativeResult = ItemStackUtil.fromSnapshotToNative(result.get().getResult());

            cir.setReturnValue(nativeResult);
        }
    }

    @Inject(method = "getSmeltingExperience", at = @At("RETURN"))
    public void onGetSmeltingExperience(ItemStack stack, CallbackInfoReturnable<Float> cir) {
        ItemStackSnapshot ingredient = ItemStackUtil.snapshotOf(stack);
        Optional<SmeltingResult> result = getCustomResult(ingredient);

        if (result.isPresent()) {
            float nativeResult = (float) result.get().getExperience();

            cir.setReturnValue(nativeResult);
        }
    }

    @Override
    public Optional<SmeltingResult> getResult(ItemStackSnapshot ingredient) {
        checkNotNull(ingredient, "ingredient");

        Optional<SmeltingResult> customResult = getCustomResult(ingredient);

        if (customResult.isPresent()) {
            return customResult;
        }

        ItemStack nativeIngredient = ItemStackUtil.fromSnapshotToNative(ingredient);
        ItemStack nativeResult = getSmeltingResult(nativeIngredient);

        if (!nativeResult.isEmpty()) {
            ItemStackSnapshot result = ItemStackUtil.snapshotOf(nativeResult);
            double experience = (double) getSmeltingExperience(nativeIngredient);

            return Optional.of(new SmeltingResult(result, experience));
        }

        return Optional.empty();
    }

    public Optional<SmeltingResult> getCustomResult(ItemStackSnapshot ingredient) {
        checkNotNull(ingredient, "ingredient");

        for (SmeltingRecipe recipe : this.customRecipes) {
            Optional<SmeltingResult> result = recipe.getResult(ingredient);

            if (result.isPresent()) {
                return result;
            }
        }

        return Optional.empty();
    }

    @Override
    public void register(SmeltingRecipe recipe) {
        checkNotNull(recipe, "recipe");
        checkArgument(!customRecipeToNativeIngredient.containsKey(recipe),
                "This recipe has already been registered!");

        ItemStackSnapshot exemplaryIngredient = recipe.getExemplaryIngredient();
        ItemStack nativeExemplaryIngredient = ItemStackUtil.fromSnapshotToNative(exemplaryIngredient);
        ItemStack nativeExemplaryResult = ItemStackUtil.fromSnapshotToNative(recipe.getExemplaryResult());
        float nativeExemplaryExperience = (float) recipe.getResult(exemplaryIngredient).get().getExperience();

        this.smeltingList.put(nativeExemplaryIngredient, nativeExemplaryResult);
        this.experienceList.put(nativeExemplaryIngredient, nativeExemplaryExperience);
        this.customRecipeToNativeIngredient.put(recipe, nativeExemplaryIngredient);
        this.nativeIngredientToCustomRecipe.put(nativeExemplaryIngredient, recipe);
        this.customRecipes.add(recipe);
    }

    @Override
    public void remove(SmeltingRecipe recipe) {
        checkNotNull(recipe, "recipe");

        ItemStack nativeExemplaryIngredient = this.customRecipeToNativeIngredient.remove(recipe);

        if (nativeExemplaryIngredient == null) {
            return;
        }

        this.nativeIngredientToCustomRecipe.remove(nativeExemplaryIngredient);
        this.smeltingList.remove(nativeExemplaryIngredient);
        this.experienceList.remove(nativeExemplaryIngredient);
        this.customRecipes.remove(recipe);
    }

    @Override
    public Collection<SmeltingRecipe> getRecipes() {
        ImmutableList.Builder<SmeltingRecipe> builder = ImmutableList.builder();

        for (Map.Entry<ItemStack, ItemStack> smeltingEntry : smeltingList.entrySet()) {
            ItemStack nativeIngredient = smeltingEntry.getKey();

            // If not a custom recipe, add first
            if (!nativeIngredientToCustomRecipe.containsKey(nativeIngredient)) {
                double experience = (double) experienceList.get(nativeIngredient);
                ItemStackSnapshot exemplaryResult = ItemStackUtil.snapshotOf(smeltingEntry.getValue());
                ItemStackSnapshot exemplaryIngredient = ItemStackUtil.snapshotOf(nativeIngredient);
                Predicate<ItemStackSnapshot> ingredientPredicate = new MatchSmeltingVanillaItemStack(exemplaryIngredient);

                builder.add(new SpongeSmeltingRecipe(exemplaryResult, exemplaryIngredient, ingredientPredicate, experience));
            }
        }

        builder.addAll(customRecipes);

        return builder.build();
    }

}
