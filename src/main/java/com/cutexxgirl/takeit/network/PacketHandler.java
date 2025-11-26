package com.cutexxgirl.takeit.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * PacketHandler - Network packet registration
 * 
 * This class manages all network communication between client and server.
 * It registers custom packets that are sent when players interact with the mod.
 */
public class PacketHandler {
    // Protocol version - must match between client and server
    private static final String PROTOCOL_VERSION = "1";
    
    // The network channel for this mod's packets
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("takeit", "main"),  // Unique channel ID
            () -> PROTOCOL_VERSION,                   // Protocol version supplier
            PROTOCOL_VERSION::equals,                 // Client version check
            PROTOCOL_VERSION::equals                  // Server version check
    );

    /**
     * Register all network packets
     * Called during mod initialization
     */
    public static void register() {
        int id = 0;  // Packet ID counter
        
        // Register radius pickup packet (sent when player presses G key)
        INSTANCE.registerMessage(id++, PacketPickup.class, 
                PacketPickup::encode,      // How to write packet to bytes
                PacketPickup::decode,      // How to read packet from bytes
                PacketPickup::handle);     // What to do when packet is received
        
        // Register click pickup packet (sent when player right-clicks an item)
        INSTANCE.registerMessage(id++, PacketClickPickup.class, 
                PacketClickPickup::encode, 
                PacketClickPickup::decode, 
                PacketClickPickup::handle);
    }
}
