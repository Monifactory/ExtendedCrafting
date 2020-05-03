package com.blakebr0.extendedcrafting.singularity;

import com.blakebr0.extendedcrafting.ExtendedCrafting;
import com.blakebr0.extendedcrafting.config.ModConfigs;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SingularityRegistry {
    private static final Logger LOGGER = LogManager.getLogger(ExtendedCrafting.NAME);
    private static final SingularityRegistry INSTANCE = new SingularityRegistry();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

    private final List<Singularity> singularities = new ArrayList<>();

    public void loadSingularities() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        File dir = FMLPaths.CONFIGDIR.get().resolve("extendedcrafting/singularities/").toFile();

        if (!dir.exists() && dir.mkdirs()) {
            for (Singularity singularity : defaults()) {
                JsonObject json = SingularityUtils.writeToJson(singularity);
                FileWriter writer = null;
                try {
                    File file = new File(dir, singularity.getId().getPath() + ".json");
                    writer = new FileWriter(file);
                    GSON.toJson(json, writer);
                    writer.close();
                } catch (Exception e) {
                    LOGGER.error("An error occurred while generating default singularities", e);
                } finally {
                    IOUtils.closeQuietly(writer);
                }
            }
        }

        if (!dir.mkdirs() && dir.isDirectory()) {
            this.loadFiles(dir);
        }

        stopwatch.stop();
        LOGGER.info("Loaded {} singularity type(s) in {} ms", this.singularities.size(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    public List<Singularity> getSingularities() {
        return this.singularities;
    }

    public Singularity getSingularityById(ResourceLocation id) {
        return this.singularities.stream().filter(s -> id.equals(s.getId())).findFirst().orElse(null);
    }

    private void loadFiles(File dir) {
        File[] files = dir.listFiles((FileFilter) FileFilterUtils.suffixFileFilter(".json"));
        if (files == null)
            return;

        for (File file : files) {
            JsonObject json;
            FileReader reader = null;
            Singularity singularity = null;
            try {
                JsonParser parser = new JsonParser();
                reader = new FileReader(file);
                json = parser.parse(reader).getAsJsonObject();
                String name = file.getName().replace(".json", "");
                singularity = SingularityUtils.loadFromJson(new ResourceLocation(ExtendedCrafting.MOD_ID, name), json);

                reader.close();
            } catch (Exception e) {
                LOGGER.error("An error occurred while loading singularities", e);
            } finally {
                IOUtils.closeQuietly(reader);
            }

            if (singularity != null) {
                ResourceLocation id = singularity.getId();
                this.singularities.removeIf(s -> id.equals(s.getId()));
                this.singularities.add(singularity);
            }
        }
    }

    public static SingularityRegistry getInstance() {
        return INSTANCE;
    }

    private static List<Singularity> defaults() {
        int count = ModConfigs.SINGULARITY_MATERIALS_REQUIRED.get();
        return Lists.newArrayList(
                new Singularity(new ResourceLocation(ExtendedCrafting.MOD_ID, "coal"), "singularity.extendedcrafting.coal", new int[] { 3289650, 1052693 }, Ingredient.fromItems(Items.COAL), count),
                new Singularity(new ResourceLocation(ExtendedCrafting.MOD_ID, "iron"), "singularity.extendedcrafting.iron", new int[] { 14211288, 11053224 }, Ingredient.fromItems(Items.IRON_INGOT), count),
                new Singularity(new ResourceLocation(ExtendedCrafting.MOD_ID, "lapis_lazuli"), "singularity.extendedcrafting.lapis_lazuli", new int[] { 5931746, 3432131 }, Ingredient.fromItems(Items.LAPIS_LAZULI), count),
                new Singularity(new ResourceLocation(ExtendedCrafting.MOD_ID, "redstone"), "singularity.extendedcrafting.redstone", new int[] { 11144961, 7471104 }, Ingredient.fromItems(Items.REDSTONE), count),
                new Singularity(new ResourceLocation(ExtendedCrafting.MOD_ID, "glowstone"), "singularity.extendedcrafting.glowstone", new int[] { 16759902, 11825472 }, Ingredient.fromItems(Items.GLOWSTONE_DUST), count),
                new Singularity(new ResourceLocation(ExtendedCrafting.MOD_ID, "diamond"), "singularity.extendedcrafting.diamond", new int[] { 4910553, 2147765 }, Ingredient.fromItems(Items.DIAMOND), count),
                new Singularity(new ResourceLocation(ExtendedCrafting.MOD_ID, "emerald"), "singularity.extendedcrafting.emerald", new int[] { 4322180, 43564 }, Ingredient.fromItems(Items.EMERALD), count),

                new Singularity(new ResourceLocation(ExtendedCrafting.MOD_ID, "aluminum"), "singularity.extendedcrafting.aluminum", new int[] { 13290714, 13290714 }, "forge:ingots/aluminum", count),
                new Singularity(new ResourceLocation(ExtendedCrafting.MOD_ID, "copper"), "singularity.extendedcrafting.copper", new int[] { 13529601, 13529601 }, "forge:ingots/copper", count),
                new Singularity(new ResourceLocation(ExtendedCrafting.MOD_ID, "tin"), "singularity.extendedcrafting.tin", new int[] { 7770277, 7770277 }, "forge:ingots/tin", count),
                new Singularity(new ResourceLocation(ExtendedCrafting.MOD_ID, "bronze"), "singularity.extendedcrafting.bronze", new int[] { 11040068, 11040068 }, "forge:ingots/bronze", count),
                new Singularity(new ResourceLocation(ExtendedCrafting.MOD_ID, "silver"), "singularity.extendedcrafting.silver", new int[] { 8628914, 8628914 }, "forge:ingots/silver", count),
                new Singularity(new ResourceLocation(ExtendedCrafting.MOD_ID, "lead"), "singularity.extendedcrafting.lead", new int[] { 4738919, 4738919 }, "forge:ingots/lead", count),
                new Singularity(new ResourceLocation(ExtendedCrafting.MOD_ID, "steel"), "singularity.extendedcrafting.steel", new int[] { 5658198, 5658198 }, "forge:ingots/steel", count),
                new Singularity(new ResourceLocation(ExtendedCrafting.MOD_ID, "nickel"), "singularity.extendedcrafting.nickel", new int[] { 12498050, 12498050 }, "forge:ingots/nickel", count),
                new Singularity(new ResourceLocation(ExtendedCrafting.MOD_ID, "electrum"), "singularity.extendedcrafting.electrum", new int[] { 10981685, 10981685 }, "forge:ingots/electrum", count),
                new Singularity(new ResourceLocation(ExtendedCrafting.MOD_ID, "invar"), "singularity.extendedcrafting.invar", new int[] { 9608599, 9608599 }, "forge:ingots/invar", count),
                new Singularity(new ResourceLocation(ExtendedCrafting.MOD_ID, "uranium"), "singularity.extendedcrafting.uranium", new int[] { 4620301, 4620301 }, "forge:ingots/uranium", count),
                new Singularity(new ResourceLocation(ExtendedCrafting.MOD_ID, "platinum"), "singularity.extendedcrafting.platinum", new int[] { 7334639, 7334639 }, "forge:ingots/platinum", count)
        );
    }
}