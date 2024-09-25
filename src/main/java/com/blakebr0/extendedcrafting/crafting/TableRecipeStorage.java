package com.blakebr0.extendedcrafting.crafting;

import com.blakebr0.cucumber.inventory.BaseItemStackHandler;
import com.blakebr0.extendedcrafting.container.inventory.ExtendedCraftingInventory;
import com.blakebr0.extendedcrafting.util.EmptyContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * The class that handles the 3 recipes stored in an auto table or variation.
 * Stores recipes in instances of BaseItemStackHandler
 */
public class TableRecipeStorage {
    private final BaseItemStackHandler[] recipes = new BaseItemStackHandler[3]; //TODO: reduce this to a single instance
    private final int slots; //Amount of slots in the inventory
    private int selected = -1; //TODO: scrap this/rework it as a boolean
    private BaseItemStackHandler selectedRecipeGrid = null; //Selected recipe. TODO: Scrap and merge with "recipes" into "recipe"

    /**
     * Creates the item stack handlers for each recipe
     * TODO: Reduce down to one recipe
     * @param slots Amount of slots in the container
     */
    public TableRecipeStorage(int slots) {
        this.slots = slots;

        for (int i = 0; i < this.recipes.length; i++) {
            this.recipes[i] = BaseItemStackHandler.create(slots);
        }
    }

    /**
     * Returns the amount of slots
     * @return The amount of slots
     */
    public int getSlots() {
        return this.slots;
    }

    /**
     * Returns the selected recipe
     * TODO: Scrap this or rework into "isActiveRecipe" or sth
     * @return The selected recipe
     */
    public int getSelected() {
        return this.selected;
    }

    /**
     * Changes the selected recipe. If trying to select the currently selected recipe, unselects instead.
     * TODO: Rework into a toggle "active"/"inactive" for the single recipe
     * @param selected The index of the newly selected recipe.
     */
    public void setSelected(int selected) {
        if (selected == this.selected || selected < -1 || selected > 2)
            selected = -1;

        this.selected = selected;

        this.updateSelectedRecipeGrid();
    }

    /**
     * Returns the recipe with the given index
     * TODO: Rework into returning the only existing recipe
     * @param index The index of the requested recipe
     * @return The recipe with the given index
     */
    public BaseItemStackHandler getRecipe(int index) {
        if (index < 0 || index >= this.recipes.length)
            return null;

        return this.recipes[index];
    }

    /**
     * Returns whether the given index has a recipe.
     * A recipe slot is considered to have a recipe if it contains a non-empty stack.
     * TODO: Rework into checking the only recipe slot.
     * @param index The index checked.
     * @return Whether there's a recipe at the given index.
     */
    public boolean hasRecipe(int index) {
        if (index < 0 || index >= this.recipes.length)
            return false;

        return !this.recipes[index].getStacks().stream().allMatch(ItemStack::isEmpty);
    }

    /**
     * Checks whether the storage has any saved recipe.
     * Returns true if at least one non-empty stack is found across any of the storage's recipe.
     * TODO: Scrap this
     * @return Whether the table contains a saved recipe.
     */
    public boolean hasRecipes() {
        return IntStream.range(0, this.recipes.length).anyMatch(this::hasRecipe);
    }

    /**
     * Saves a recipe into a storage slot according to a given container's contents.
     * @param index The index of the recipe being checked TODO: Remove index
     * @param inventory The inventory being copied into the recipe slot
     * @param output The output of the recipe, given separately from the rest of the content
     */
    public void setRecipe(int index, Container inventory, ItemStack output) {
        var recipe = BaseItemStackHandler.create(this.slots);

        for (int i = 0; i < this.slots - 1; i++) {
            recipe.setStackInSlot(i, inventory.getItem(i).copy());
        }

        recipe.setStackInSlot(this.slots - 1, output);

        this.recipes[index] = recipe;
    }

    /**
     * Clears the recipe slot with the given index
     * @param index the index to clear TODO: Remove index
     */
    public void unsetRecipe(int index) {
        if (index < 0 || index >= this.recipes.length)
            return;

        this.recipes[index] = BaseItemStackHandler.create(this.slots);

        if (index == this.selected) {
            this.setSelected(-1);
        }
    }

    /**
     * Returns the list of recipes stored
     * TODO: Scrap, redundant with only one storage slot
     * @return The list of recipes
     */
    public BaseItemStackHandler[] getRecipes() {
        return this.recipes;
    }

    /**
     * Returns the number of stored recipes.
     * TODO: Scrap, redundant with only one storage slot
     * @return The number of stored recipes.
     */
    public int getRecipeCount() {
        return Arrays.stream(this.recipes).mapToInt(recipe -> recipe.getStacks().stream().allMatch(ItemStack::isEmpty) ? 0 : 1).sum();
    }

    /**
     * Returns the currently selected recipe
     * TODO: Scrap, redundant with only one storage slot
     * @return The currently selected recipe
     */
    public BaseItemStackHandler getSelectedRecipe() {
        if (this.selected < 0 || this.selected > this.recipes.length)
            return null;

        return this.recipes[this.selected];
    }

    /**
     * Returns selected recipe grid. Null if none is selected.
     * A recipe grid is the list of ingredients, without the output. Primarily used for item import logic.
     * @return the currently selected recipe grid.
     */
    public BaseItemStackHandler getSelectedRecipeGrid() {
        return this.selectedRecipeGrid;
    }

    /**
     * Encodes TableRecipeStorage data for saving purposes.
     * TODO: Adapt to only one recipe
     * @return CompoundTag containing the object's data
     */
    public CompoundTag serializeNBT() {
        var recipes = new ListTag();

        for (int i = 0; i < this.recipes.length; i++) {
            recipes.add(i, this.recipes[i].serializeNBT());
        }

        var tag = new CompoundTag();

        tag.put("Recipes", recipes);
        tag.putInt("Selected", this.selected);

        return tag;
    }

    /**
     * Runs on load, processes the tag's data to put it back into the object
     * TODO: Adapt to one recipe
     * @param tag The tag being processed
     */
    public void deserializeNBT(CompoundTag tag) {
        var recipes = tag.getList("Recipes", Tag.TAG_COMPOUND);

        for (int i = 0; i < recipes.size(); i++) {
            this.recipes[i].deserializeNBT(recipes.getCompound(i));
        }

        this.selected = tag.getInt("Selected");

        this.updateSelectedRecipeGrid();
    }

    /**
     * On load, checks the validity of recipe outputs by trying to find a match for the grid and assembling it.
     * @param level Networking shit I think TODO: Learn more about levels
     * @param type Recipe type. Basically what the attached container can run.
     */
    public void onLoad(Level level, RecipeType<? extends Recipe<Container>> type) {
        for (int i = 0; i < this.recipes.length; i++) {
            if (this.hasRecipe(i)) {
                var recipe = this.recipes[i];
                var grid = this.createRecipeGrid(recipe);
                var inventory = new ExtendedCraftingInventory(EmptyContainer.INSTANCE, grid, 3);
                var result = level.getRecipeManager().getRecipeFor(type, inventory, level)
                        .map(r -> r.assemble(inventory, level.registryAccess()))
                        .orElse(ItemStack.EMPTY);

                recipe.setStackInSlot(this.slots - 1, result);
            }
        }
    }

    /**
     * Sets the selected recipe grid to the currently selected stored recipe.
     * Not sure why there's a need for a distinction. TODO: Figure out why.
     */
    private void updateSelectedRecipeGrid() {
        if (this.selected > -1) {
            var recipe = this.recipes[this.selected];

            this.selectedRecipeGrid = this.createRecipeGrid(recipe);
        } else {
            this.selectedRecipeGrid = null;
        }
    }

    /**
     * Creates a recipe grid from a recipe, which seems to be the stored recipe without its output? TODO: Confirm
     * @param recipe The recipe
     * @return An ItemStackHandler containing the same items, minus the output. Not sure of its use
     */
    private BaseItemStackHandler createRecipeGrid(BaseItemStackHandler recipe) {
        var grid = BaseItemStackHandler.create(this.slots - 1);

        for (int i = 0; i < this.slots - 1; i++) {
            grid.setStackInSlot(i, recipe.getStackInSlot(i));
        }

        return grid;
    }
}
