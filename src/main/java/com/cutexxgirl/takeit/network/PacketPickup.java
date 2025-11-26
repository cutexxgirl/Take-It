package com.cutexxgirl.takeit.network;

import com.cutexxgirl.takeit.TakeItConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

/**
 * PacketPickup - Network packet for radius item pickup
 * 
 * This packet is sent from client to server when the player presses the pickup key (default: G).
 * It triggers pickup of all items within a configurable radius around the player.
 * 
 * Client -> Server communication
 */
public class PacketPickup {
    
    /**
     * Default constructor - no data needs to be sent
     * The server knows who sent the packet from the context
     */
    public PacketPickup() {
    }

    /**
     * Constructor for decoding from network buffer
     * @param buf The buffer (unused, no data in this packet)
     */
    public PacketPickup(FriendlyByteBuf buf) {
    }

    /**
     * Encode the packet to bytes
     * @param buf The buffer to write to (unused, no data to send)
     */
    public void encode(FriendlyByteBuf buf) {
    }

    /**
     * Decode the packet from bytes
     * @param buf The buffer to read from
     * @return A new PacketPickup instance
     */
    public static PacketPickup decode(FriendlyByteBuf buf) {
        return new PacketPickup(buf);
    }

    /**
     * Handle the packet when received on the server
     * This finds all items within the configured radius and picks them up
     * 
     * @param contextSupplier Supplier for the network context
     */
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && TakeItConfig.ENABLE_MOD.get()) {
                // Get configured pickup radius
                int radiusX = TakeItConfig.RADIUS_X.get();
                int radiusY = TakeItConfig.RADIUS_Y.get();
                int radiusZ = TakeItConfig.RADIUS_Z.get();

                // Create a bounding box around the player with the configured radius
                AABB searchBox = new AABB(
                        player.getX() - radiusX, player.getY() - radiusY, player.getZ() - radiusZ,
                        player.getX() + radiusX, player.getY() + radiusY, player.getZ() + radiusZ
                );

                // Find all ItemEntities within the search box
                List<ItemEntity> items = player.level().getEntitiesOfClass(ItemEntity.class, searchBox);

                // Pick up each item
                for (ItemEntity item : items) {
                    if (item.isAlive()) {
                        // Set flag to bypass automatic pickup cancellation
                        com.cutexxgirl.takeit.events.CommonEvents.isManualPickup.set(true);
                        try {
                            // Reset pickup delay (items have delay when dropped)
                            item.setPickUpDelay(0);
                            // Trigger vanilla pickup logic
                            item.playerTouch(player);
                        } catch (Exception e) {
                            // Log error but don't crash the game
                            e.printStackTrace();
                        } finally {
                            // Always reset the flag
                            com.cutexxgirl.takeit.events.CommonEvents.isManualPickup.set(false);
                        }
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}
