package com.witherschat.extendedcrafting.client;

import com.witherschat.extendedcrafting.init.ModRecipeTypes;
import net.minecraft.client.RecipeBookCategories;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class ModRecipeBookCategories {
    @SubscribeEvent
    public void onRegisterRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
        ModRecipeTypes.COMBINATION.ifPresent(type -> event.registerRecipeCategoryFinder(type, recipe -> RecipeBookCategories.UNKNOWN));
        ModRecipeTypes.TABLE.ifPresent(type -> event.registerRecipeCategoryFinder(type, recipe -> RecipeBookCategories.UNKNOWN));
        ModRecipeTypes.COMPRESSOR.ifPresent(type -> event.registerRecipeCategoryFinder(type, recipe -> RecipeBookCategories.UNKNOWN));
        ModRecipeTypes.ENDER_CRAFTER.ifPresent(type -> event.registerRecipeCategoryFinder(type, recipe -> RecipeBookCategories.UNKNOWN));
        ModRecipeTypes.FLUX_CRAFTER.ifPresent(type -> event.registerRecipeCategoryFinder(type, recipe -> RecipeBookCategories.UNKNOWN));
    }
}
