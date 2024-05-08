package net.mob_armor_trims.majo24.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.mob_armor_trims.majo24.MobArmorTrims;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    public static final int DEFAULT_TRIM_CHANCE = 50;
    public static final int DEFAULT_STACKED_TRIM_CHANCE = 25;
    public static final int DEFAULT_MAX_STACKED_TRIMS = 10;

    private final Config config;
    public final Path configPath;
    public static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
                .setPrettyPrinting()
                .create();

    public ConfigManager(Config config, Path configPath) {
        this.config = config;
        this.configPath = configPath;
    }

    public static Config getConfigFromFile(Path configPath) {
        if (!Files.exists(configPath)) {
            Config newConfig = new Config(DEFAULT_TRIM_CHANCE, DEFAULT_STACKED_TRIM_CHANCE, DEFAULT_MAX_STACKED_TRIMS);
            try {
                MobArmorTrims.LOGGER.info("Creating config file");
                Files.createFile(configPath);
                String jsonConfig = GSON.toJson(newConfig);
                Files.writeString(configPath, jsonConfig);
            } catch (IOException e) {
                MobArmorTrims.LOGGER.error("Could not create config file", e);
            }
            return newConfig;
        } else {
            String jsonConfig;
            try {
                MobArmorTrims.LOGGER.info("Reading config file");
                jsonConfig = new String(Files.readAllBytes(configPath));
                return GSON.fromJson(jsonConfig, Config.class);
            } catch (IOException e) {
                MobArmorTrims.LOGGER.error("Could not read config file", e);
                return new Config(DEFAULT_TRIM_CHANCE, DEFAULT_STACKED_TRIM_CHANCE, DEFAULT_MAX_STACKED_TRIMS);
            }
        }
    }

    public void saveConfig() {
        MobArmorTrims.LOGGER.info("Saving config file");
        String jsonConfig = GSON.toJson(config);
        try {
            Files.writeString(configPath, jsonConfig);
        } catch (IOException e) {
            MobArmorTrims.LOGGER.error("Could not save config file", e);
        }
    }

    public int getTrimChance() {
        return this.config.getTrimChance();
    }

    public void setTrimChance(int trimChance) {
        this.config.setTrimChance(trimChance);
    }

    public int getStackedTrimChance() {
        return this.config.getStackedTrimChance();
    }

    public void setStackedTrimChance(int stackedTrimChance) {this.config.setStackedTrimChance(stackedTrimChance);}

    public int getMaxStackedTrims() {return this.config.getMaxStackedTrims();}

    public void setMaxStackedTrims(int maxStackedTrims) {this.config.setMaxStackedTrims(maxStackedTrims);}
}