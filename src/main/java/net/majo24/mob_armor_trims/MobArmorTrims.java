package net.majo24.mob_armor_trims;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.majo24.mob_armor_trims.config.ConfigManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.util.math.random.Random;

public class MobArmorTrims implements ModInitializer {
	public static final String MOD_ID = "mob_armor_trims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final ConfigManager configManager = new ConfigManager(ConfigManager.getConfigFromFile());

	@Override
	public void onInitialize() {
	}

    public static void randomlyApplyRandomTrims(DynamicRegistryManager registryManager, Random random, Iterable<ItemStack> equippedArmor) {
        boolean stackedArmorTrimsIsLoaded = FabricLoader.getInstance().isModLoaded("stacked_trims");

        RegistryKey<Registry<ArmorTrimMaterial>> materialKey = RegistryKeys.TRIM_MATERIAL;
        Registry<ArmorTrimMaterial> materialRegistry = registryManager.get(materialKey);
        RegistryKey<Registry<ArmorTrimPattern>> patternKey = RegistryKeys.TRIM_PATTERN;
        Registry<ArmorTrimPattern> patternRegistry = registryManager.get(patternKey);

        for (ItemStack armor : equippedArmor) {
            if (configManager.getTrimChance() < random.nextInt(100)) {continue;}
            if (armor.getItem() != Items.AIR) {
                applyRandomTrim(registryManager, materialRegistry, patternRegistry, random, armor);

                // Stacked Armor Trims Compatibility
                if (stackedArmorTrimsIsLoaded) {
                    int appliedArmorTrims = 0;
                    while ((configManager.getStackedTrimChance() >= random.nextInt(100)) && (appliedArmorTrims < configManager.getMaxStackedTrims())) {
                        applyRandomTrim(registryManager, materialRegistry, patternRegistry, random, armor);
                        appliedArmorTrims++;
                    }
                }
            }
        }
	}

    private static void applyRandomTrim(DynamicRegistryManager registryManager, Registry<ArmorTrimMaterial> materialRegistry, Registry<ArmorTrimPattern> patternRegistry, Random random, ItemStack armor) {
                RegistryEntry.Reference<ArmorTrimMaterial> randomTrimMaterial = materialRegistry.getRandom(random).get();
                RegistryEntry.Reference<ArmorTrimPattern> randomTrimPattern = patternRegistry.getRandom(random).get();

                ArmorTrim armorTrim = new ArmorTrim(randomTrimMaterial, randomTrimPattern);
                ArmorTrim.apply(registryManager, armor, armorTrim);
    }
}