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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.smelting.SmeltingRecipe;
import org.spongepowered.api.item.recipe.smelting.SmeltingRecipeRegistry;
import org.spongepowered.api.item.recipe.smelting.SmeltingResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
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
public class MixinFurnaceRecipes implements SmeltingRecipeRegistry {
    private final List<SmeltingRecipe> recipes = Lists.newArrayList();

    /**
     * @author Limeth - 17. 1. 2017
     * @reason Instead of using the {@link Map}s in {@link FurnaceRecipes}, we
     *         mixin and maintain a list of {@link SmeltingRecipe}s. This is to
     *         allow recipe resolution via ingredient predicates, which are not
     *         normally supported in vanilla.
     */
    @Overwrite
    public void addSmeltingRecipe(ItemStack input, ItemStack stack, float experience) {
        ItemStackSnapshot exemplaryResult = ItemStackUtil.snapshotOf(stack);
        ItemStackSnapshot exemplaryIngredient = ItemStackUtil.snapshotOf(input);
        Predicate<ItemStackSnapshot> ingredientPredicate = new MatchSmeltingVanillaItemStack(exemplaryIngredient);

        this.recipes.add(new SpongeSmeltingRecipe(exemplaryResult, exemplaryIngredient, ingredientPredicate, experience));
    }

    /**
     * @author Limeth - 17. 1. 2017
     * @reason Instead of using the {@link Map}s in {@link FurnaceRecipes}, we
     *         mixin and maintain a list of {@link SmeltingRecipe}s. This is to
     *         allow recipe resolution via ingredient predicates, which are not
     *         normally supported in vanilla.
     */
    @Overwrite
    public ItemStack getSmeltingResult(ItemStack stack) {
        ItemStackSnapshot ingredient = ItemStackUtil.snapshotOf(stack);
        Optional<SmeltingResult> result = getResult(ingredient);

        //noinspection OptionalIsPresent
        if (result.isPresent()) {
            return ItemStackUtil.fromSnapshotToNative(result.get().getResult());
        } else {
            return ItemStack.EMPTY;
        }
    }

    /**
     * @author Limeth - 17. 1. 2017
     * @reason Instead of using the {@link Map}s in {@link FurnaceRecipes}, we
     *         mixin and maintain a list of {@link SmeltingRecipe}s. This is to
     *         allow recipe resolution via ingredient predicates, which are not
     *         normally supported in vanilla.
     */
    @Overwrite
    public float getSmeltingExperience(ItemStack stack) {
        ItemStackSnapshot ingredient = ItemStackUtil.snapshotOf(stack);
        Optional<SmeltingResult> result = getResult(ingredient);

        //noinspection OptionalIsPresent
        if (result.isPresent()) {
            return (float) result.get().getExperience();
        } else {
            return 0.0F;
        }
    }

    /**
     * @author Limeth - 17. 1. 2017
     * @reason Instead of using the {@link Map}s in {@link FurnaceRecipes}, we
     *         mixin and maintain a list of {@link SmeltingRecipe}s. This is to
     *         allow recipe resolution via ingredient predicates, which are not
     *         normally supported in vanilla.
     */
    @Overwrite
    public Map<ItemStack, ItemStack> getSmeltingList() {
        // `ItemStack` doesn't implement `equals` and `hashCode`
        Map<ItemStack, ItemStack> map = new IdentityHashMap<>(this.recipes.size());

        for (SmeltingRecipe recipe : this.recipes) {
            ItemStack ingredient = ItemStackUtil.fromSnapshotToNative(recipe.getExemplaryIngredient());
            ItemStack result = ItemStackUtil.fromSnapshotToNative(recipe.getExemplaryResult());

            map.put(ingredient, result);
        }

        return Collections.unmodifiableMap(map);
    }

    @Override
    @Nonnull
    public Optional<SmeltingResult> getResult(@Nonnull ItemStackSnapshot ingredient) {
        checkNotNull(ingredient, "ingredient");

        for (SmeltingRecipe recipe : this.recipes) {
            Optional<SmeltingResult> result = recipe.getResult(ingredient);

            if (result.isPresent()) {
                return result;
            }
        }

        return Optional.empty();
    }

    @Override
    public void register(@Nonnull SmeltingRecipe recipe) {
        checkNotNull(recipe, "recipe");

        this.recipes.add(recipe);
    }

    @Override
    public void remove(@Nonnull SmeltingRecipe recipe) {
        checkNotNull(recipe, "recipe");

        this.recipes.remove(recipe);
    }

    @Override
    @Nonnull
    public Collection<SmeltingRecipe> getRecipes() {
        return this.recipes;
    }
}
