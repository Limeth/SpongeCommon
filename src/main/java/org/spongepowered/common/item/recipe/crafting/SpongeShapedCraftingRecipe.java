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

import net.minecraft.item.crafting.ShapedRecipes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.item.recipe.crafting.ShapedCraftingRecipe;
import org.spongepowered.api.world.World;
import org.spongepowered.common.SpongeImplHooks;
import org.spongepowered.common.item.inventory.util.ItemStackUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SpongeShapedCraftingRecipe extends ShapedRecipes implements ShapedCraftingRecipe {

    SpongeShapedCraftingRecipe(int width, int height, ItemStackSnapshot result, List<String> aisle,
                               Map<Character, Predicate<ItemStackSnapshot>> ingredientPredicates) {
        super(width, height, new net.minecraft.item.ItemStack[0], ItemStackUtil.fromSnapshotToNative(result));
    }

    @Override
    public boolean isValid(GridInventory grid, World world) {
        int gapWidth = grid.getColumns() - getWidth();
        int gapHeight = grid.getRows() - getHeight();

        // Shift the aisle along the grid wherever possible
        for (int offsetX = 0; offsetX <= gapWidth; offsetX++) {
            byShiftingTheAisle:
            for (int offsetY = 0; offsetY <= gapHeight; offsetY++) {
                // Test each predicate in the aisle
                for (int aisleX = 0; aisleX < getWidth(); aisleX++) {
                    for (int aisleY = 0; aisleY < getHeight(); aisleY++) {
                        final int finalAisleX = aisleX;
                        final int finalAisleY = aisleY;
                        int gridX = aisleX + offsetX;
                        int gridY = aisleY + offsetY;
                        Slot slot = grid.getSlot(gridX, gridY)
                                .orElseThrow(() -> new IllegalStateException("Could not access the slot," +
                                        " even though it was supposed to be in bounds."));
                        ItemStack itemStack = slot.peek()
                                .orElseThrow(() -> new IllegalStateException("Could not access the ItemStack" +
                                        " in the slot as it's currently unavailable."));
                        Predicate<ItemStackSnapshot> ingredientPredicate = getIngredientPredicate(aisleX, aisleY)
                                .orElseThrow(() -> new IllegalStateException("The ingredient predicate at [" +
                                        finalAisleX + "; " + finalAisleY + "] is not present. There should be" +
                                        " one for each symbol in the aisle!"));

                        if (!ingredientPredicate.test(itemStack.createSnapshot()))
                            continue byShiftingTheAisle;
                    }
                }

                // Make sure the gap is empty
                for (int gapX = 0; gapX < gapWidth; gapX++) {
                    for (int gapY = 0; gapY < gapHeight; gapY++) {
                        int gridX = gapX + (gapX >= offsetX ? getWidth() : 0);
                        int gridY = gapY + (gapY >= offsetY ? getHeight() : 0);
                        boolean empty = grid.getSlot(gridX, gridY)
                                .flatMap(Slot::peek)
                                .map(itemStack -> itemStack.getItem() == ItemTypes.NONE)
                                .orElse(true);

                        if (!empty)
                            continue byShiftingTheAisle;
                    }
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public Optional<ItemStack> getResult(GridInventory grid, World world) {
        if(!isValid(grid, world))
            return Optional.empty();

        return Optional.of(getExemplaryResult().createStack());
    }

    @Override
    public Optional<List<ItemStack>> getRemainingItems(GridInventory grid, World world) {
        if(!isValid(grid, world))
            return Optional.empty();

        return Optional.of(StreamSupport.stream(grid.<Slot>slots().spliterator(), false)
                .map(Slot::peek)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(SpongeImplHooks::getContainerItem)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList()));
    }

    @Override
    public ItemStackSnapshot getExemplaryResult() {
        return null;
    }

    @Override
    public Optional<Predicate<ItemStackSnapshot>> getIngredientPredicate(int x, int y) {
        return null;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }
}
