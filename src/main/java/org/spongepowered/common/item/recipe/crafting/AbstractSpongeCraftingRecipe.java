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

import static org.spongepowered.common.item.recipe.crafting
        .TemporaryUtilClass.toSpongeInventory;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe;
import org.spongepowered.api.world.World;
import org.spongepowered.common.item.inventory.util.ItemStackUtil;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractSpongeCraftingRecipe implements CraftingRecipe, IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, net.minecraft.world.World worldIn) {
        return matches(this::isValid, inv, worldIn);
    }

    public static boolean matches(BiFunction<GridInventory, World, Boolean> isValid, InventoryCrafting inv, net.minecraft.world.World worldIn) {
        return isValid.apply(toSpongeInventory(inv), (World) worldIn);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return getCraftingResult(this::getResult, inv);
    }

    public static ItemStack getCraftingResult(Function<GridInventory, ItemStackSnapshot> getResult, InventoryCrafting inv) {
        return ItemStackUtil.fromSnapshotToNative(getResult.apply(toSpongeInventory(inv)));
    }

    @Override
    public int getRecipeSize() {
        return getRecipeSize(this::getSize);
    }

    public static int getRecipeSize(Supplier<Integer> getSize) {
        return getSize.get();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return getRecipeOutput(this::getExemplaryResult);
    }

    public static ItemStack getRecipeOutput(Supplier<ItemStackSnapshot> getExemplaryResult) {
        return ItemStackUtil.fromSnapshotToNative(getExemplaryResult.get());
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        return getRemainingItems(this::getRemainingItems, inv);
    }

    public static NonNullList<ItemStack> getRemainingItems(Function<GridInventory, List<ItemStackSnapshot>> getRemainingItems, InventoryCrafting inv) {
        NonNullList<ItemStack> result = NonNullList.create();

        getRemainingItems.apply(toSpongeInventory(inv))
                .stream()
                .map(ItemStackUtil::fromSnapshotToNative)
                .forEach(result::add);

        return result;
    }
}