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

import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import org.spongepowered.api.item.recipe.RecipeRegistry;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe;
import org.spongepowered.asm.mixin.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Mixin(CraftingManager.class)
@Implements(@Interface(iface = RecipeRegistry.class, prefix = "sponge$"))
public abstract class MixinCraftingManager {

    @Shadow
    @Final
    private List<IRecipe> recipes;

    public void sponge$register(CraftingRecipe recipe) {
        spongeRecipes().add(recipe);
    }

    public void sponge$remove(CraftingRecipe recipe) {
        spongeRecipes().remove(recipe);
    }

    public Collection<CraftingRecipe> sponge$getRecipes() {
        return Collections.unmodifiableList(spongeRecipes());
    }

    @SuppressWarnings("unchecked")
    private List<CraftingRecipe> spongeRecipes() {
        return (List<CraftingRecipe>) (List<?>) recipes;
    }

}