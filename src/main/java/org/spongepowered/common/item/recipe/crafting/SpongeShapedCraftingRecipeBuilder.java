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

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.ShapedCraftingRecipe;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class SpongeShapedCraftingRecipeBuilder implements ShapedCraftingRecipe.Builder {

    private final List<String> aisle = Lists.newArrayList();
    private final Map<Character, Predicate<ItemStackSnapshot>> ingredientMap = Maps.newHashMap();
    private ItemStackSnapshot result;

    @Nonnull
    @Override
    public ShapedCraftingRecipe.Builder aisle(@Nullable String... aisle) {
        this.aisle.clear();

        if (aisle != null) {
            Collections.addAll(this.aisle, aisle);
        }

        return this;
    }

    @Nonnull
    @Override
    public ShapedCraftingRecipe.Builder where(char symbol, @Nullable Predicate<ItemStackSnapshot> ingredient) throws IllegalArgumentException {
        checkState(!this.aisle.isEmpty(), "aisle must be set before setting aisle symbols");

        if (ingredient != null) {
            ingredientMap.put(symbol, ingredient);
        } else {
            ingredientMap.remove(symbol);
        }

        return this;
    }

    @Nonnull
    @Override
    public ShapedCraftingRecipe.Builder where(char symbol, @Nullable ItemStackSnapshot ingredient) throws IllegalArgumentException {
        return where(symbol, ingredient != null ? new MatchesVanillaItemStack(ingredient) : null);
    }

    @Nonnull
    @Override
    public ShapedCraftingRecipe.Builder result(@Nullable ItemStackSnapshot result) {
        this.result = result;
        return this;
    }

    @Nonnull
    @Override
    public ShapedCraftingRecipe build() {
        checkState(!this.aisle.isEmpty(), "aisle has not been set");
        checkState(!this.ingredientMap.isEmpty(), "no ingredients set");
        checkState(this.result != null, "no result set");

        ImmutableTable.Builder<Integer, Integer, Predicate<ItemStackSnapshot>> tableBuilder = ImmutableTable.builder();
        Iterator<String> aisleIterator = this.aisle.iterator();
        String aisleRow = aisleIterator.next();
        int width = aisleRow.length();
        int height = 0;

        checkState(width > 0, "The aisle cannot be empty.");

        do {
            checkState(aisleRow.length() == width,
                    "The aisle has an inconsistent width.");

            for (int x = 0; x < width; x++) {
                char symbol = aisleRow.charAt(x);
                Predicate<ItemStackSnapshot> ingredientPredicate = ingredientMap.get(symbol);

                if (ingredientPredicate != null) {
                    tableBuilder.put(x, height, ingredientPredicate);
                }
            }

            height++;
            aisleRow = aisleIterator.next();
        } while(aisleIterator.hasNext());

        height++;

        return new SpongeShapedCraftingRecipe(width, height, result, tableBuilder.build());
    }

    @Nonnull
    @Override
    public ShapedCraftingRecipe.Builder from(@Nullable ShapedCraftingRecipe value) {
        this.aisle.clear();
        this.ingredientMap.clear();

        if (value != null) {
            for (int y = 0; y < value.getHeight(); y++) {
                String row = "";

                for (int x = 0; x < value.getWidth(); x++) {
                    char symbol = (char) ('a' + x + y * value.getWidth());
                    row += symbol;

                    value.getIngredientPredicate(x, y)
                            .ifPresent(predicate -> ingredientMap.put(symbol, predicate));
                }

                this.aisle.add(row);
            }

            this.result = value.getExemplaryResult();
        } else {
            this.result = null;
        }

        return this;
    }

    @Nonnull
    @Override
    public ShapedCraftingRecipe.Builder reset() {
        this.aisle.clear();
        this.ingredientMap.clear();
        this.result = null;
        return this;
    }

}
