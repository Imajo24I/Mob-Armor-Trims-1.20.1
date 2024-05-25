package net.mob_armor_trims.majo24.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.mob_armor_trims.majo24.MobArmorTrims;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ConfigManager {
    public static final Config.TrimSystem DEFAULT_ENABLED_SYSTEM = Config.TrimSystem.RANDOM_TRIMS;
    public static final int DEFAULT_TRIM_CHANCE = 50;
    public static final int DEFAULT_SIMILAR_TRIM_CHANCE = 75;
    public static final int DEFAULT_NO_TRIMS_CHANCE = 25;

    public static final List<Config.CustomTrim> DEFAULT_CUSTOM_TRIMS_LIST = new ArrayList<>();
    public static final boolean DEFAULT_APPLY_TO_ENTIRE_ARMOR = true;

    public static final int DEFAULT_STACKED_TRIM_CHANCE = 10;
    public static final int DEFAULT_MAX_STACKED_TRIMS = 3;

    private final Config config;
    private final Map<List<String>, ArmorTrim> cachedCustomTrims;

    public final Path configPath;

    public ConfigManager(Config config, Path configPath) {
        this.config = config;
        this.configPath = configPath;
        this.cachedCustomTrims = new HashMap<>();
    }

    public static Config getConfigFromFile(Path configPath) {
        if (Files.exists(configPath)) {
            // Get config from file
            MobArmorTrims.LOGGER.info("Reading Mob Armor Trims config file");
            CommentedFileConfig fileConfig = CommentedFileConfig.of(configPath.toFile());
            fileConfig.load();
            return configFromFileConfig(fileConfig);
        } else {
            // Create a new Config
            Config newConfig = getDefaultConfig();
            try {
                MobArmorTrims.LOGGER.info("Creating Mob Armor Trims config file");
                Files.createFile(configPath);
                CommentedFileConfig fileConfig = fileConfigFromConfig(newConfig, configPath);
                fileConfig.save();
            } catch (IOException e) {
                MobArmorTrims.LOGGER.error("Could not create Mob Armor Trims config file", e);
            }
            return newConfig;
        }
    }

    private static CommentedFileConfig fileConfigFromConfig(Config config, Path configPath) {
        CommentedFileConfig fileConfig = CommentedFileConfig.of(new File(configPath.toString()));
        fileConfig.add("enabled_system", config.getEnabledSystem());
        fileConfig.add("no_trims_chance", config.getNoTrimsChance());
        fileConfig.add("trim_chance", config.getTrimChance());
        fileConfig.add("similar_trim_chance", config.getSimilarTrimChance());
        fileConfig.add("apply_to_entire_armor", config.getApplyToEntireArmor());
        fileConfig.add("custom_trims_list", Config.CustomTrim.toStringList(config.getCustomTrimsList()));
        fileConfig.add("stacked_trim_chance", config.getStackedTrimChance());
        fileConfig.add("max_stacked_trims", config.getMaxStackedTrims());
        return fileConfig;
    }

    private static Config configFromFileConfig(CommentedFileConfig fileConfig) {
        return new Config(
                Config.TrimSystem.valueOf(fileConfig.get("enabled_system")), fileConfig.get("trim_chance"),
                fileConfig.get("similar_trim_chance"), fileConfig.get("no_trims_chance"),
                Config.CustomTrim.fromList(fileConfig.get("custom_trims_list")), fileConfig.get("apply_to_entire_armor"),
                fileConfig.get("stacked_trim_chance"), fileConfig.get("max_stacked_trims")
        );
    }

    public static Config getDefaultConfig() {
        return new Config(DEFAULT_ENABLED_SYSTEM, DEFAULT_TRIM_CHANCE, DEFAULT_SIMILAR_TRIM_CHANCE, DEFAULT_NO_TRIMS_CHANCE,
                DEFAULT_CUSTOM_TRIMS_LIST, DEFAULT_APPLY_TO_ENTIRE_ARMOR,
                DEFAULT_STACKED_TRIM_CHANCE, DEFAULT_MAX_STACKED_TRIMS);
    }

    public void saveConfig() {
        MobArmorTrims.LOGGER.info("Saving Mob Armor Trims config to file");
        CommentedFileConfig fileConfig = fileConfigFromConfig(this.config, configPath);
        fileConfig.save();
    }

    public Config.TrimSystem getEnabledSystem() {
        return this.config.getEnabledSystem();
    }

    public void setEnabledSystem(Config.TrimSystem enabledSystem) {
        this.config.setEnabledSystem(enabledSystem);
    }

    public int getTrimChance() {
        return this.config.getTrimChance();
    }

    public void setTrimChance(int trimChance) {
        this.config.setTrimChance(trimChance);
    }

    public int getSimilarTrimChance() { return this.config.getSimilarTrimChance(); }

    public void setSimilarTrimChance(int sameTrimChance) { this.config.setSimilarTrimChance(sameTrimChance); }

    public int getNoTrimsChance() { return this.config.getNoTrimsChance(); }

    public void setNoTrimsChance(int noTrimsChance) { this.config.setNoTrimsChance(noTrimsChance); }

    /**
     * @return Returns random Custom Trim out of the Custom Trims List
    */
    @Nullable
    public Config.CustomTrim getCustomTrim(RandomSource random) {
        List<Config.CustomTrim> customTrimsList = this.config.getCustomTrimsList();
        if (customTrimsList.isEmpty()) {
            return null;
        } else {
            return customTrimsList.get(random.nextInt(customTrimsList.size()));
        }
    }

    public List<Config.CustomTrim> getCustomTrimsList() {
        return config.getCustomTrimsList();
    }

    public void setCustomTrimsList(List<Config.CustomTrim> customTrimsList) {
        this.config.setCustomTrimsList(customTrimsList);
    }

    public void addCustomTrimToCache(String material, String pattern, ArmorTrim trim) {
        this.cachedCustomTrims.put(Arrays.asList(material, pattern), trim);
    }

    @Nullable
    public ArmorTrim getOrCreateCachedCustomTrim(String material, String pattern, RegistryAccess registryAccess) {
        ArmorTrim cachedTrim = this.cachedCustomTrims.get(Arrays.asList(material, pattern));
        if (cachedTrim == null) {
            ArmorTrim newTrim = new Config.CustomTrim(material, pattern).getTrim(registryAccess);
            if (newTrim == null) {
                return null;
            }
            this.addCustomTrimToCache(material, pattern, newTrim);
            return newTrim;
        }
        return cachedTrim;
    }

    public boolean getApplyToEntireArmor() {
        return this.config.getApplyToEntireArmor();
    }

    public void setApplyToEntireArmor(boolean applyToEntireArmor) {
        this.config.setApplyToEntireArmor(applyToEntireArmor);
    }

    public int getStackedTrimChance() {
        return this.config.getStackedTrimChance();
    }

    public void setStackedTrimChance(int stackedTrimChance) { this.config.setStackedTrimChance(stackedTrimChance); }

    public int getMaxStackedTrims() {return this.config.getMaxStackedTrims();}

    public void setMaxStackedTrims(int maxStackedTrims) { this.config.setMaxStackedTrims(maxStackedTrims); }
}