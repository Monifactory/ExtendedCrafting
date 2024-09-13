package com.blakebr0.extendedcrafting.api.crafting;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Used to represent an Extended Crafting Table recipe for the recipe type
 */
public interface ITableRecipe extends Recipe<Container> {

    /**
     * Returns whether the recipe is tier-locked
     * @return Whether the recipe is tier-locked
     */
    int getTier();

    /**
     * Returns whether the recipe is tier-locked
     * @return Whether the recipe is tier-locked
     */
    boolean hasRequiredTier();
}
