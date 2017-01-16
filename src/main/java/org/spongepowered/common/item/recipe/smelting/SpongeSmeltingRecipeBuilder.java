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
package org.spongepowered.common.item.recipe.smelting;

import static com.google.common.base.Preconditions.*;

import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.smelting.SmeltingRecipe;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

public class SpongeSmeltingRecipeBuilder implements SmeltingRecipe.Builder {
    private ItemStackSnapshot exemplaryResult;
    private ItemStackSnapshot exemplaryIngredient;
    private Predicate<ItemStackSnapshot> ingredientPredicate;
    private Double experience;

    @Override
    @Nonnull
    public SmeltingRecipe.Builder from(@Nonnull SmeltingRecipe value) {
        this.exemplaryResult = value.getExemplaryResult();
        this.exemplaryIngredient = value.getExemplaryIngredient();
        this.experience = null;

        return this;
    }

    @Override
    @Nonnull
    public SmeltingRecipe.Builder reset() {
        this.exemplaryResult = null;
        this.exemplaryIngredient = null;
        this.experience = null;

        return this;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    @Nonnull
    public SmeltingRecipe.Builder result(@Nonnull ItemStackSnapshot result) {
        checkNotNull(result, "result");
        checkArgument(result != ItemStackSnapshot.NONE, "The result must not be ItemStackSnapshot.NONE.");

        this.exemplaryResult = result;

        return this;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    @Nonnull
    public SmeltingRecipe.Builder ingredient(@Nonnull Predicate<ItemStackSnapshot> ingredientPredicate, @Nonnull ItemStackSnapshot exemplaryIngredient) {
        checkNotNull(ingredientPredicate, "ingredientPredicate");
        checkNotNull(exemplaryIngredient, "exemplaryIngredient");
        checkArgument(exemplaryIngredient != ItemStackSnapshot.NONE, "The ingredient must not be ItemStackSnapshot.NONE.");
        checkState(ingredientPredicate.test(exemplaryIngredient), "The ingredient predicate does not allow the specified exemplary ingredient.");

        this.ingredientPredicate = ingredientPredicate;
        this.exemplaryIngredient = exemplaryIngredient;

        return this;
    }

    @Override
    @Nonnull
    public SmeltingRecipe.Builder ingredient(@Nonnull ItemStackSnapshot ingredient) {
        checkNotNull(ingredient, "ingredient");

        return ingredient(new MatchSmeltingVanillaItemStack(ingredient), ingredient);
    }

    @Override
    @Nonnull
    public SmeltingRecipe.Builder experience(double experience) {
        checkState(experience >= 0, "The experience must be non-negative.");

        this.experience = experience;

        return this;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    @Nonnull
    public SmeltingRecipe build() {
        checkState(exemplaryResult != null && exemplaryResult != ItemStackSnapshot.NONE,
                "The result must be specified.");
        checkState(exemplaryIngredient != null && exemplaryIngredient != ItemStackSnapshot.NONE,
                "The ingredient must be specified.");
        checkState(ingredientPredicate != null, "You must specify the ingredient predicate.");
        checkState(ingredientPredicate.test(exemplaryIngredient), "The ingredient predicate does not allow the specified exemplary ingredient.");
        checkState(experience != null, "You must specify the amount of experience released by this recipe.");
        checkState(experience >= 0, "The experience must be non-negative.");

        return new SpongeSmeltingRecipe(exemplaryResult, exemplaryIngredient, ingredientPredicate, experience);
    }
}
