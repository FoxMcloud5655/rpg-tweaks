package io.redspace.ironsrpgtweaks.config;

import io.redspace.ironsrpgtweaks.IronsRpgTweaks;
import io.redspace.ironsrpgtweaks.durability_module.DeathDurabilityMode;
import io.redspace.ironsrpgtweaks.durability_module.VanillaDurabilityMode;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;

import java.util.Set;

public class ConfigHelper {
    public static class Durability {
        public static boolean shouldTakeVanillaDamage(ItemStack itemStack) {
            IronsRpgTweaks.LOGGER.debug("shouldTakeVanillaDamage: {}", itemStack);
            if (!ServerConfigs.DURABILITY_MODULE_ENABLED.get()) {
                return true;
            }
            // Check whitelist/blacklist before "mode == NONE" because mitigating user error is more important than critical perfection
            if (ServerConfigs.DURABILITY_VANILLA_MODE_BLACKLIST_ITEMS.contains(itemStack.getItem())) {
                IronsRpgTweaks.LOGGER.debug("item is blacklisted: false");
                return false;
            }
            if (!ServerConfigs.DURABILITY_VANILLA_MODE_WHITELIST_ITEMS.isEmpty()) {
                IronsRpgTweaks.LOGGER.debug("items are whitelisted: ServerConfigs.DURABILITY_VANILLA_MODE_WHITELIST_ITEMS.contains(itemStack.getItem()): {}", ServerConfigs.DURABILITY_VANILLA_MODE_WHITELIST_ITEMS.contains(itemStack.getItem()));
                return ServerConfigs.DURABILITY_VANILLA_MODE_WHITELIST_ITEMS.contains(itemStack.getItem());
            }
            var mode = ServerConfigs.DURABILITY_VANILLA_MODE.get();
            if (mode == VanillaDurabilityMode.NONE) {
                IronsRpgTweaks.LOGGER.debug("mode = VanillaDurabilityMode.NONE: false");
                return false;
            }
            if (mode == VanillaDurabilityMode.ALL) {
                IronsRpgTweaks.LOGGER.debug("mode == VanillaDurabilityMode.ALL: true");
                return true;
            }
            IronsRpgTweaks.LOGGER.debug("is armor/tool: {}: {}", mode, itemStack.getItem() instanceof ArmorItem ? mode == VanillaDurabilityMode.ARMOR : mode == VanillaDurabilityMode.TOOLS);
            return itemStack.getItem() instanceof ArmorItem ? mode == VanillaDurabilityMode.ARMOR : mode == VanillaDurabilityMode.TOOLS;
        }

        public static boolean shouldTakeDeathDamage(ItemStack itemStack) {
            if (!ServerConfigs.DURABILITY_MODULE_ENABLED.get()) {
                return false;
            }
            // Check whitelist/blacklist before "mode == NONE" because mitigating user error is more important than critical perfection
            if (ServerConfigs.DURABILITY_DEATH_MODE_BLACKLIST_ITEMS.contains(itemStack.getItem())) {
                IronsRpgTweaks.LOGGER.debug("item is blacklisted: false");
                return false;
            }
            if (!ServerConfigs.DURABILITY_DEATH_MODE_WHITELIST_ITEMS.isEmpty()) {
                IronsRpgTweaks.LOGGER.debug("items are whitelisted: ServerConfigs.DURABILITY_VANILLA_MODE_WHITELIST_ITEMS.contains(itemStack.getItem()): {}", ServerConfigs.DURABILITY_VANILLA_MODE_WHITELIST_ITEMS.contains(itemStack.getItem()));
                return ServerConfigs.DURABILITY_DEATH_MODE_WHITELIST_ITEMS.contains(itemStack.getItem());
            }
            DeathDurabilityMode mode = ServerConfigs.DURABILITY_DEATH_MODE.get();
            if (mode == DeathDurabilityMode.NONE) {
                return false;
            }
            if (mode == DeathDurabilityMode.ALL) {
                return true;
            }
            return itemStack.getItem() instanceof ArmorItem ? mode == DeathDurabilityMode.ARMOR : mode == DeathDurabilityMode.TOOLS;
        }

        public static boolean shouldHideDurabilityBar(ItemStack itemStack) {
            if (!ServerConfigs.DURABILITY_MODULE_ENABLED.get()) {
                return false;
            }
            return (!shouldTakeDeathDamage(itemStack) || (ServerConfigs.ADDITIONAL_DURABILITY_LOST_ON_DEATH.get() == 0 && ServerConfigs.DURABILITY_LOST_ON_DEATH.get() == 0))
                    && (!shouldTakeVanillaDamage(itemStack));
        }
    }

    public static class Damage {
        public static Set<EntityType<? extends Entity>> damageEntityBlacklist;
    }

    public static class Hunger {
        public static double useDurationMultiplier(Item item) {
            if (ServerConfigs.HUNGER_MODULE_ENABLED.get()) {
                if (item.isEdible()) {
                    return ServerConfigs.EAT_TIME_MULTIPLIER.get();
                } else if (item instanceof PotionItem) {
                    return ServerConfigs.POTION_DRINK_TIME_MULTIPLER.get();
                }
            }
            return 1.0;
        }

        public static boolean shouldDisableVanillaHunger() {
            return ServerConfigs.HUNGER_DISABLED.get() && ServerConfigs.HUNGER_MODULE_ENABLED.get();
        }

        public static Set<Item> foodStackBlacklist;
    }

}
