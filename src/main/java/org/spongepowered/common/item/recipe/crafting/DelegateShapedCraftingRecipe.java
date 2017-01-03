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
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.item.recipe.crafting.ShapedCraftingRecipe;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DelegateShapedCraftingRecipe extends AbstractSpongeShapedCraftingRecipe {

    private final ShapedCraftingRecipe delegate;

    public DelegateShapedCraftingRecipe(ShapedCraftingRecipe delegate) {
        Preconditions.checkNotNull(delegate, "The delegate must not be null");

        this.delegate = delegate;
    }

    @Override
    public ItemStackSnapshot getExemplaryResult() {
        return delegate.getExemplaryResult();
    }

    @Override
    public List<String> getAisle() {
        return delegate.getAisle();
    }

    @Override
    public Map<Character, Predicate<ItemStackSnapshot>> getIngredientPredicates() {
        return delegate.getIngredientPredicates();
    }

    @Override
    public Optional<Predicate<ItemStackSnapshot>> getIngredientPredicate(char symbol) {
        return delegate.getIngredientPredicate(symbol);
    }

    @Override
    public Optional<Predicate<ItemStackSnapshot>> getIngredientPredicate(int x, int y) {
        return delegate.getIngredientPredicate(x, y);
    }

    @Override
    public int getWidth() {
        return delegate.getWidth();
    }

    @Override
    public int getHeight() {
        return delegate.getHeight();
    }

    @Override
    public boolean isValid(GridInventory grid, World world) {
        return delegate.isValid(grid, world);
    }

    @Override
    public Optional<ItemStack> getResult(GridInventory grid, World world) {
        return delegate.getResult(grid, world);
    }

    @Override
    public Stream<Object> getRemainingItems(GridInventory grid, World world) {
        return delegate.getRemainingItems(grid, world);
    }

}
