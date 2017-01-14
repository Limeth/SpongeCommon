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
package org.spongepowered.common.item.recipe.crafting;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.ShapelessCraftingRecipe;

import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpongeShapelessCraftingRecipeBuilder implements ShapelessCraftingRecipe.Builder {

    private ItemStackSnapshot exemplaryResult = ItemStackSnapshot.NONE;
    private List<Predicate<ItemStackSnapshot>> ingredients = Lists.newArrayList();

    @Nonnull
    @Override
    public ShapelessCraftingRecipe.Builder from(@Nonnull ShapelessCraftingRecipe value) {
        this.exemplaryResult = value.getExemplaryResult();

        if (exemplaryResult == null) {
            this.exemplaryResult = ItemStackSnapshot.NONE;
        }

        this.ingredients.clear();
        this.ingredients.addAll(value.getIngredientPredicates());

        return this;
    }

    @Nonnull
    @Override
    public ShapelessCraftingRecipe.Builder reset() {
        this.exemplaryResult = ItemStackSnapshot.NONE;
        this.ingredients.clear();

        return this;
    }

    @Nonnull
    @Override
    public ShapelessCraftingRecipe.Builder addIngredientPredicate(@Nullable Predicate<ItemStackSnapshot> ingredient) {
        Preconditions.checkNotNull(ingredient, "ingredient");
        this.ingredients.add(ingredient);

        return this;
    }

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    @Override
    public ShapelessCraftingRecipe.Builder addIngredientPredicate(@Nonnull ItemStackSnapshot ingredient) {
        return addIngredientPredicate(ingredient != ItemStackSnapshot.NONE ? new MatchesVanillaItemStack(ingredient) : null);
    }

    @Nonnull
    @Override
    public ShapelessCraftingRecipe.Builder result(@Nullable ItemStackSnapshot result) {
        Preconditions.checkNotNull(result, "result");

        this.exemplaryResult = result;

        return this;
    }

    @Nonnull
    @Override
    public ShapelessCraftingRecipe build() {
        return new SpongeShapelessCraftingRecipe(exemplaryResult, ingredients);
    }

}
