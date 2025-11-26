package com.cutexxgirl.takeit.network;

import com.cutexxgirl.takeit.TakeItConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * PacketClickPickup - Network packet for right-click item pickup
 * 
 * This packet is sent from client to server when a player right-clicks on an item.
 * It contains the entity ID of the item to be picked up.
 * 
 * Client -> Server communication
 */
public class PacketClickPickup {
    // The ID of the entity (item) to pick up
    private final int entityId;

    /**
     * Constructor for creating a new pickup packet
     * @param entityId The ID of the ItemEntity to pick up
     */
    public PacketClickPickup(int entityId) {
        this.entityId = entityId;
    }

    /**
     * Constructor for decoding packet from network data
     * @param buf The buffer containing packet data
     */
    public PacketClickPickup(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
    }

    /**
     * Encode the packet into bytes for network transmission
     * @param buf The buffer to write data to
     */
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
    }

    /**
     * Decode the packet from bytes received from network
     * @param buf The buffer containing received data
     * @return A new PacketClickPickup instance
     */
    public static PacketClickPickup decode(FriendlyByteBuf buf) {
        return new PacketClickPickup(buf);
    }

    /**
     * Handle the packet when received on the server
     * This executes on the server thread and performs the actual item pickup
     * 
     * @param contextSupplier Supplier for the network context
     */
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && TakeItConfig.ENABLE_MOD.get()) {
                com.mojang.logging.LogUtils.getLogger().info("TakeIt: Server received click pickup packet for entity ID: {}", entityId);
                
                // Get the entity from the world
                Entity target = player.level().getEntity(entityId);
                com.mojang.logging.LogUtils.getLogger().info("TakeIt: Found entity: {}", target);
                
                if (target instanceof ItemEntity itemEntity) {
                    double distSq = player.distanceToSqr(itemEntity);
                    com.mojang.logging.LogUtils.getLogger().info("TakeIt: Distance squared: {}", distSq);
                    
                    // Check if player is within reach (6 blocks)
                    if (distSq < 36.0D) {
                        // Set the manual pickup flag to prevent automatic pickup event from canceling
                        com.cutexxgirl.takeit.events.CommonEvents.isManualPickup.set(true);
                        try {
                            // Reset pickup delay so item can be picked up immediately
                            itemEntity.setPickUpDelay(0);
                            // Trigger vanilla pickup logic
                            itemEntity.playerTouch(player);
                            com.mojang.logging.LogUtils.getLogger().info("TakeIt: Successfully triggered playerTouch");
                        } catch (Exception e) {
                            com.mojang.logging.LogUtils.getLogger().error("TakeIt: Error during pickup", e);
                            e.printStackTrace();
                        } finally {
                            // Always reset the flag, even if pickup failed
                            com.cutexxgirl.takeit.events.CommonEvents.isManualPickup.set(false);
                        }
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}
