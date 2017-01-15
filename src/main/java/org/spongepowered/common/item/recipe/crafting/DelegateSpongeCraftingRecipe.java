package org.spongepowered.common.item.recipe.crafting;

import com.google.common.base.Preconditions;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe;
import org.spongepowered.api.world.World;

import java.util.List;

import javax.annotation.Nonnull;

public class DelegateSpongeCraftingRecipe extends AbstractSpongeCraftingRecipe {

    private final CraftingRecipe recipe;

    public DelegateSpongeCraftingRecipe(CraftingRecipe recipe) {
        Preconditions.checkNotNull(recipe, "recipe");

        this.recipe = recipe;
    }

    @Override
    @Nonnull
    public ItemStackSnapshot getExemplaryResult() {
        return this.recipe.getExemplaryResult();
    }

    @Override
    public boolean isValid(@Nonnull GridInventory grid, @Nonnull World world) {
        return this.recipe.isValid(grid, world);
    }

    @Override
    @Nonnull
    public ItemStackSnapshot getResult(@Nonnull GridInventory grid) {
        return this.recipe.getResult(grid);
    }

    @Override
    @Nonnull
    public List<ItemStackSnapshot> getRemainingItems(@Nonnull GridInventory grid) {
        return this.recipe.getRemainingItems(grid);
    }

    @Override
    public int getSize() {
        return this.recipe.getSize();
    }

}
