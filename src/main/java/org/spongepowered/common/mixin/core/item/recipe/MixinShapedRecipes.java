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
package org.spongepowered.common.mixin.core.item.recipe;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.ShapedCraftingRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Mixin(ShapedRecipes.class)
@Implements(@Interface(iface = ShapedCraftingRecipe.class, prefix = "sponge$"))
public abstract class MixinShapedRecipes implements IRecipe, IMixinShapedRecipes {

    @Final
    @Shadow
    private int recipeWidth;

    @Final
    @Shadow
    private int recipeHeight;

    private List<String> aisle;
    private Map<Character, Predicate<ItemStackSnapshot>> ingredientPredicates;

    public List<String> sponge$getAisle() {
        return aisle;
    }

    @Override
    public void setAisle(List<String> aisle) {
        Preconditions.checkArgument(aisle.size() == recipeHeight, "Invalid aisle height");

        for(String row : aisle)
            if(row.length() != recipeWidth)
                throw new IllegalArgumentException("Invalid aisle width (inconsistent?)");

        this.aisle = ImmutableList.copyOf(aisle);
    }

    public Map<Character, Predicate<ItemStackSnapshot>> sponge$getIngredientPredicates() {
        return ingredientPredicates;
    }

    public Optional<Predicate<ItemStackSnapshot>> sponge$getIngredientPredicate(char symbol) {
        return Optional.ofNullable(ingredientPredicates.get(symbol));
    }

    public Optional<Predicate<ItemStackSnapshot>> sponge$getIngredientPredicate(int x, int y) {
        if (x < 0 || x >= sponge$getWidth() || y < 0 || y >= sponge$getHeight())
            return Optional.empty();

        char symbol = sponge$getAisle().get(y).charAt(x);

        return sponge$getIngredientPredicate(symbol);
    }

    @Override
    public void setIngredientPredicates(Map<Character, Predicate<ItemStackSnapshot>> ingredientPredicates) {
        this.ingredientPredicates = ImmutableMap.copyOf(ingredientPredicates);
    }

    public int sponge$getWidth() {
        return recipeWidth;
    }

    public int sponge$getHeight() {
        return recipeHeight;
    }

}
