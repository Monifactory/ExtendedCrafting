package com.blakebr0.extendedcrafting.crafting.recipe;

import com.blakebr0.extendedcrafting.init.ModItems;
import com.blakebr0.extendedcrafting.init.ModRecipeSerializers;
import com.blakebr0.extendedcrafting.singularity.SingularityRegistry;
import com.blakebr0.extendedcrafting.singularity.SingularityUtils;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.items.IItemHandler;

/**
 * Special shapeless recipe type for the Ultimate Singularity
 * with extra utility methods.
 */
public class UltimateSingularityRecipe extends ShapelessTableRecipe {
    private static boolean ingredientsLoaded = false;

    /**
     * Creates an instance of the UltimateSingularityRecipe.
     * Something noteworthy is that the inputs list is a currently empty
     * yet MUTABLE list, which allows UltimateSingularityRecipe::getIngredients
     * to define the inputs.
     * The recipe is always locked to table tier 5.
     * @param recipeId Recipe ID (resource location)
     * @param output Recipe output
     */
    public UltimateSingularityRecipe(ResourceLocation recipeId, ItemStack output) {
        super(recipeId, NonNullList.create(), output, 5);
    }

    /**
     * If ingredients are loaded, just returns a list of input.
     * If ingredients aren't loaded yet, dynamically loads a list of singularities
     * as inputs.
     * @return List of recipe ingredients
     */
    @Override
    public NonNullList<Ingredient> getIngredients() {
        if (!ingredientsLoaded) {
            super.getIngredients().clear();

            SingularityRegistry.getInstance().getSingularities()
                    .stream()
                    .filter(singularity -> singularity.isInUltimateSingularity() && singularity.getIngredient() != Ingredient.EMPTY)
                    .limit(121)
                    .map(SingularityUtils::getItemForSingularity)
                    .map(Ingredient::of)
                    .forEach(super.getIngredients()::add);

            ingredientsLoaded = true;
        }

        return super.getIngredients();
    }

    /**
     * Checks whether the contents of the table matches the grid,
     * but only returns true if the list of ingredients,
     * which is dynamically generated, is not empty.
     * @param inventory The table's inventory
     * @return Whether the contents of the table match the recipe
     */
    @Override
    public boolean matches(IItemHandler inventory) {
        // ensure ingredients list is initialized
        var ingredients = this.getIngredients();

        // in the case there are no ingredients, the recipe should never match
        return !ingredients.isEmpty() && super.matches(inventory);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.ULTIMATE_SINGULARITY.get();
    }

    /**
     * Invalidates the recipe's ingredients, prompting
     * ::getIngredients to load them again.
     */
    public static void invalidate() {
        ingredientsLoaded = false;
    }

    /**
     * Recipe serializer. Defines:
     * - Serializer::fromJson
     * - Serializer::fromNetwork
     * - Serializer::toNetwork
     */
    public static class Serializer implements RecipeSerializer<UltimateSingularityRecipe> {

        /**
         * Creates a UltimateSingularityRecipe with the given recipeId.
         * Effectively ignores the contents of the JSON object.
         * @param recipeId Recipe ID
         * @param json JSON object that doesn't contain the recipe's data since it's auto generated
         * @return Recipe for the Ultimate Singularity
         */
        @Override
        public UltimateSingularityRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            return new UltimateSingularityRecipe(recipeId, new ItemStack(ModItems.ULTIMATE_SINGULARITY.get()));
        }

        /**
         * Just recreates the recipe since it's always the same.
         * @param recipeId Recipe ID
         * @param buffer Useless, please ignore.
         * @return Recipe for the Ultimate Singularity
         */
        @Override
        public UltimateSingularityRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            return new UltimateSingularityRecipe(recipeId, new ItemStack(ModItems.ULTIMATE_SINGULARITY.get()));
        }

        /**
         * Doesn't encode anything. The recipe is recreated in ::fromNetwork anyway.
         * *visible disbelief*
         * @param buffer Useless, please ignore
         * @param recipe Useless, please ignore
         */
        @Override
        public void toNetwork(FriendlyByteBuf buffer, UltimateSingularityRecipe recipe) { }
    }
}
