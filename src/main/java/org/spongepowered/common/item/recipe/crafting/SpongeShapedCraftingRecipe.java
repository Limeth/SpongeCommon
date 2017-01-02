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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.common.item.inventory.util.ItemStackUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class SpongeShapedCraftingRecipe extends AbstractSpongeShapedCraftingRecipe {

    private final int width;
    private final int height;
    private final List<String> aisle;
    private final Map<Character, Predicate<ItemStackSnapshot>> ingredientPredicates;

    SpongeShapedCraftingRecipe(int width, int height, ItemStackSnapshot result, List<String> aisle,
                               Map<Character, Predicate<ItemStackSnapshot>> ingredientPredicates) {
        this.width = width;
        this.height = height;
        this.aisle = ImmutableList.copyOf(aisle);
        this.ingredientPredicates = ImmutableMap.copyOf(ingredientPredicates);
    }

    @Override
    public ItemStackSnapshot getExemplaryResult() {
        return ItemStackUtil.snapshotOf(getRecipeOutput());
    }

    @Override
    public List<String> getAisle() {
        return aisle;
    }

    @Override
    public Map<Character, Predicate<ItemStackSnapshot>> getIngredientPredicates() {
        return ingredientPredicates;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public Optional<ItemStack> getResult(GridInventory grid) {
        return Optional.of(getExemplaryResult().createStack());
    }
}
