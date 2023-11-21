package io.redspace.ironsrpgtweaks;

import com.mojang.logging.LogUtils;
import io.redspace.ironsrpgtweaks.config.ClientConfig;
import io.redspace.ironsrpgtweaks.config.ServerConfigs;
import io.redspace.ironsrpgtweaks.registry.EntityRegistry;
import io.redspace.ironsrpgtweaks.registry.ItemRegistry;
import io.redspace.ironsrpgtweaks.registry.LootRegistry;
import io.redspace.ironsrpgtweaks.registry.SoundRegistry;
import io.redspace.ironsrpgtweaks.setup.ModSetup;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(IronsRpgTweaks.MODID)
public class IronsRpgTweaks {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "irons_rpg_tweaks";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public IronsRpgTweaks() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfigs.SPEC, String.format("%s-server.toml", IronsRpgTweaks.MODID));
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC, String.format("%s-client.toml", IronsRpgTweaks.MODID));

        // Register the commonSetup method for modloading
        modEventBus.addListener(ModSetup::init);
        EntityRegistry.register(modEventBus);
        ItemRegistry.register(modEventBus);
        SoundRegistry.register(modEventBus);
        LootRegistry.register(modEventBus);

//        modEventBus.addListener(CommonHungerEvents::changeStackSize);

        MinecraftForge.EVENT_BUS.register(this);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    public static ResourceLocation id(@NotNull String path) {
        return new ResourceLocation(IronsRpgTweaks.MODID, path);
    }
}
