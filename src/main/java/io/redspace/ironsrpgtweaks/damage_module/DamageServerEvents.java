package io.redspace.ironsrpgtweaks.damage_module;

import java.util.List;

import io.redspace.ironsrpgtweaks.config.ConfigHelper;
import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class DamageServerEvents {
    public static final List<String> BLACKLIST_DAMAGE_SOURCES = List.of("lava", "inFire", "cactus", "inWall", "hotFloor", "lightningBolt", "sweetBerryBush", "outOfWorld", "drown");
    public static final List<String> BLACKLIST_ENTITY_TYPES = List.of("minecraft:slime", "minecraft:ender_dragon", "minecraft:magma_cube", "irons_spellbooks:wall_of_fire", "irons_spellbooks:void_tentacle");

    @SubscribeEvent
    public static void onRecieveDamage(LivingAttackEvent event) {
        if (shouldProcess(event.getSource(), event.getEntity()) && testDamageSource(event.getSource()) && event.getEntity().invulnerableTime > 0) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onTakeDamage(LivingDamageEvent event) {
        if (shouldProcess(event.getSource(), event.getEntity()) && testDamageSource(event.getSource())) {
            event.getEntity().invulnerableTime = ServerConfigs.IFRAME_COUNT.get();
        }
    }

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        Level level = event.getEntity().level();
        if (level.isClientSide) {
            return;
        }
        if (!(event.getEntity() instanceof FakePlayer) && event.getEntity().getAttackStrengthScale(0) < ServerConfigs.MINIMUM_ATTACK_STRENGTH.get()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void modifyKnockback(LivingKnockBackEvent event) {
        if (ServerConfigs.DAMAGE_MODULE_ENABLED.get()) {
            event.setStrength((float) (event.getStrength() * ServerConfigs.KNOCKBACK_MODIFIER.get()));
        }
    }

    private static boolean shouldProcess(DamageSource source, LivingEntity entityBeingAttacked) {
        if (ServerConfigs.DAMAGE_MODULE_ENABLED.get()) {
            return
                    !(entityBeingAttacked instanceof Player)
                            || ServerConfigs.PLAYER_DAMAGE_MODE.get() == PlayerDamageMode.ALL
                            || (ServerConfigs.PLAYER_DAMAGE_MODE.get() == PlayerDamageMode.ONLY_LIVING && (source.getDirectEntity() instanceof LivingEntity)
                    );
        }
        return false;
    }

    private static boolean testDamageSource(DamageSource source) {
        //Some damage sources rely on damage tick to apply dot. We therefore do not want to cancel the damage tick in these cases
        if (ServerConfigs.DAMAGE_MODULE_DAMAGE_SOURCE_BLACKLIST.get().contains(source.getMsgId())) {
            return false;
        }
        return (source.getEntity() == null || !ConfigHelper.Damage.damageEntityBlacklist.contains(source.getEntity().getType())) &&
                (source.getDirectEntity() == null || !ConfigHelper.Damage.damageEntityBlacklist.contains(source.getDirectEntity().getType()));
    }
}
