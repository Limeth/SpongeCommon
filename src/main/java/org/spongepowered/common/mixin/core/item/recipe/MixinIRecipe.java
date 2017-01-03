/*
 * This file is part of SpongeAPI, licensed under the MIT License (MIT).
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

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe;
import org.spongepowered.api.world.World;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.common.item.inventory.util.ItemStackUtil;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.Optional;

@Mixin(IRecipe.class)
@Implements(@Interface(iface = CraftingRecipe.class, prefix = "sponge$"))
public interface MixinIRecipe {

    default ItemStackSnapshot sponge$getExemplaryResult() {
        return ItemStackUtil.snapshotOf(getRecipeOutput());
    }

    default boolean sponge$isValid(GridInventory grid, World world) {
        // TODO convert inventories
        throw new NotImplementedException();
    }

    default Optional<ItemStack> sponge$getResult(GridInventory grid, World world) {
        // TODO convert inventories
        throw new NotImplementedException();
    }

    default Optional<List<ItemStack>> sponge$getRemainingItems(GridInventory grid, World world) {
        // TODO convert inventories
        throw new NotImplementedException();
    }

    default int sponge$getRecipeSize() {
        return getRecipeSize();
    }

    @Shadow
    boolean matches(InventoryCrafting inv, net.minecraft.world.World worldIn);

    @Shadow
    net.minecraft.item.ItemStack getCraftingResult(InventoryCrafting inv);

    @Shadow
    int getRecipeSize();

    @Shadow
    net.minecraft.item.ItemStack getRecipeOutput();

    @Shadow
    NonNullList<net.minecraft.item.ItemStack> getRemainingItems(InventoryCrafting inv);

}
