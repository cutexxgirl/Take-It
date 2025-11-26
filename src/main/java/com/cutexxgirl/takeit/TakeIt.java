package com.cutexxgirl.takeit;

import com.cutexxgirl.takeit.events.ClientEvents;
import com.cutexxgirl.takeit.events.CommonEvents;
import com.cutexxgirl.takeit.network.PacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * TakeIt - Main mod class
 * 
 * This mod changes the item pickup mechanics in Minecraft:
 * - Disables automatic pickup when walking over items
 * - Enables click-to-pickup with right mouse button
 * - Enables radius pickup with configurable key (default: G)
 * 
 * @author cutexxgirl
 * @version 1.0.0
 */
@Mod(TakeIt.MOD_ID)
public class TakeIt {
    // Mod identifier used throughout the mod
    public static final String MOD_ID = "takeit";

    /**
     * Constructor - called when the mod is loaded
     * Sets up configuration, event handlers, and registers components
     */
    public TakeIt() {
        // Get the mod event bus for registering mod-specific events
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register the common setup method to be called during mod initialization
        modEventBus.addListener(this::commonSetup);
        
        // Register the mod's configuration file (takeit-common.toml)
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TakeItConfig.SPEC);
        
        // Register common event handlers (works on both client and server)
        MinecraftForge.EVENT_BUS.register(CommonEvents.class);
    }

    /**
     * Common setup method - called during mod initialization
     * Used to register networking packets that communicate between client and server
     * 
     * @param event The common setup event
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        // Register network packets for client-server communication
        PacketHandler.register();
    }
}
