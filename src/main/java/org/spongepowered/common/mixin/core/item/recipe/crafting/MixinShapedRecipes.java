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
package org.spongepowered.common.mixin.core.item.recipe.crafting;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.ShapedCraftingRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.common.item.inventory.util.ItemStackUtil;
import org.spongepowered.common.item.recipe.crafting.MatchesVanillaItemStack;

import java.util.Optional;
import java.util.function.Predicate;

@Mixin(ShapedRecipes.class)
public abstract class MixinShapedRecipes implements IRecipe, ShapedCraftingRecipe {

    @Shadow @Final protected int recipeWidth;
    @Shadow @Final protected int recipeHeight;
    @Shadow @Final private net.minecraft.item.ItemStack[] recipeItems;

    @Override
    public Optional<Predicate<ItemStackSnapshot>> getIngredientPredicate(int x, int y) {
        if (x < 0 || x >= recipeWidth || y < 0 || y >= recipeHeight) {
            return Optional.empty();
        }

        int recipeItemIndex = x + y * recipeWidth;
        ItemStackSnapshot recipeSnapshot = ItemStackUtil.snapshotOf(recipeItems[recipeItemIndex]);

        return Optional.of(new MatchesVanillaItemStack(recipeSnapshot));
    }

    @Override
    public int getWidth() {
        return recipeWidth;
    }

    @Override
    public int getHeight() {
        return recipeHeight;
    }

}
