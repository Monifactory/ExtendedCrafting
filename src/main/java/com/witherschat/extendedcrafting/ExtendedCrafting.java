package com.witherschat.extendedcrafting;

import com.blakebr0.cucumber.helper.ConfigHelper;
import com.witherschat.extendedcrafting.client.ModRecipeBookCategories;
import com.witherschat.extendedcrafting.client.handler.ColorHandler;
import com.witherschat.extendedcrafting.config.ModConfigs;
import com.witherschat.extendedcrafting.crafting.DynamicRecipeManager;
import com.witherschat.extendedcrafting.init.ModBlocks;
import com.witherschat.extendedcrafting.init.ModContainerTypes;
import com.witherschat.extendedcrafting.init.ModCreativeModeTabs;
import com.witherschat.extendedcrafting.init.ModLootItemFunctionTypes;
import com.witherschat.extendedcrafting.init.ModItems;
import com.witherschat.extendedcrafting.init.ModRecipeSerializers;
import com.witherschat.extendedcrafting.init.ModRecipeTypes;
import com.witherschat.extendedcrafting.init.ModReloadListeners;
import com.witherschat.extendedcrafting.init.ModTileEntities;
import com.witherschat.extendedcrafting.network.NetworkHandler;
import com.witherschat.extendedcrafting.singularity.SingularityRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(ExtendedCrafting.MOD_ID)
public final class ExtendedCrafting {
	public static final String MOD_ID = "extendedcrafting";
	public static final String NAME = "Extended Crafting: Unofficial";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

	public ExtendedCrafting() {
		var bus = FMLJavaModLoadingContext.get().getModEventBus();

		bus.register(this);

		ModBlocks.REGISTRY.register(bus);
		ModItems.REGISTRY.register(bus);
		ModCreativeModeTabs.REGISTRY.register(bus);
		ModLootItemFunctionTypes.REGISTRY.register(bus);
		ModTileEntities.REGISTRY.register(bus);
		ModContainerTypes.REGISTRY.register(bus);
		ModRecipeTypes.REGISTRY.register(bus);
		ModRecipeSerializers.REGISTRY.register(bus);

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			bus.register(new ColorHandler());
			bus.register(new ModRecipeBookCategories());
		});

		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ModConfigs.CLIENT);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModConfigs.COMMON);

		ConfigHelper.load(ModConfigs.COMMON, "extendedcrafting-common.toml");
	}

	@SubscribeEvent
	public void onCommonSetup(FMLCommonSetupEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new ModReloadListeners());
		MinecraftForge.EVENT_BUS.register(DynamicRecipeManager.getInstance());
		MinecraftForge.EVENT_BUS.register(SingularityRegistry.getInstance());

		event.enqueueWork(() -> {
			NetworkHandler.onCommonSetup();
		});

		SingularityRegistry.getInstance().writeDefaultSingularityFiles();
	}

	@SubscribeEvent
	public void onClientSetup(FMLClientSetupEvent event) {
		ModTileEntities.onClientSetup();
		ModContainerTypes.onClientSetup();
	}
}
