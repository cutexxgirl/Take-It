package com.cutexxgirl.takeit.events;

import com.cutexxgirl.takeit.TakeIt;
import com.cutexxgirl.takeit.TakeItConfig;
import com.cutexxgirl.takeit.network.PacketClickPickup;
import com.cutexxgirl.takeit.network.PacketHandler;
import com.cutexxgirl.takeit.network.PacketPickup;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

/**
 * ClientEvents - Client-side only event handlers
 *
 * This class handles events that only occur on the client (player's computer):
 * - Registers the pickup keybinding
 * - Detects when pickup key is pressed
 * - Detects when right mouse button is pressed on an item
 * - Sends packets to server to trigger pickup
 */
@Mod.EventBusSubscriber(modid = TakeIt.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {

    /**
     * The keybinding for radius pickup (default: G key)
     * Players can change this in the controls menu
     */
    public static final KeyMapping PICKUP_KEY = new KeyMapping(
            "key.takeit.pickup",              // Translation key (defined in lang files)
            KeyConflictContext.IN_GAME,       // Only works when in-game (not in menus)
            InputConstants.Type.KEYSYM,       // Keyboard key type
            GLFW.GLFW_KEY_G,                  // Default key: G
            "key.categories.takeit"           // Category in controls menu
    );

    public static final KeyMapping PICKUP_ITEM_KEY = new KeyMapping(
            "key.takeit.pickupitem",          // Translation key (defined in lang files)
            KeyConflictContext.IN_GAME,       // Only works when in-game (not in menus)
            InputConstants.Type.MOUSE,       // Keyboard key type
            GLFW.GLFW_MOUSE_BUTTON_2,         // Default key: Right Click
            "key.categories.takeit"           // Category in controls menu
    );

    /**
     * Register the keybinding
     * Called during mod initialization
     *
     * @param event The key registration event
     */
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(PICKUP_KEY);
        event.register(PICKUP_ITEM_KEY);
    }

    /**
     * ForgeEvents - Event handlers that run every tick/frame
     * These are registered on the Forge event bus (not mod bus)
     */
    @Mod.EventBusSubscriber(modid = TakeIt.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onClientTick(net.minecraftforge.event.TickEvent.ClientTickEvent event) {
            Minecraft mc = Minecraft.getInstance();

            if ((mc.player == null || mc.screen != null) && !TakeItConfig.ENABLE_MOD.get()) return;

            if (PICKUP_KEY.consumeClick()) {
                // Send packet to server to trigger radius pickup
                PacketHandler.INSTANCE.sendToServer(new PacketPickup());
            }

            if (PICKUP_ITEM_KEY.consumeClick()) {
                HitResult hit = pick(mc.player, 20.0F);

                if (hit != null && hit.getType() == HitResult.Type.ENTITY) {
                    EntityHitResult entityHit = (EntityHitResult) hit;
                    if (entityHit.getEntity() instanceof ItemEntity) {
                        // Send packet to server to pick up this specific item
                        PacketHandler.INSTANCE.sendToServer(new PacketClickPickup(entityHit.getEntity().getId()));
                        LogUtils.getLogger().info("TakeIt: Sent click pickup packet for entity ID: {}", entityHit.getEntity().getId());
                    }
                }
            }
        }

        /**
         * Custom raycast to find items the player is looking at
         *
         * Why custom raycast?
         * - Minecraft's default raycast ignores ItemEntity
         * - We need to specifically detect items for click-to-pickup
         *
         * @param player The player doing the raycasting
         * @param partialTicks Fraction of a tick (for smooth interpolation)
         * @return The hit result, or null if no item was hit
         */
        private static net.minecraft.world.phys.HitResult pick(net.minecraft.world.entity.player.Player player, float partialTicks) {
            // Get the player's reach distance (how far they can interact)
            double reach = player.getAttributeValue(net.minecraftforge.common.ForgeMod.BLOCK_REACH.get());
            if (reach == 0) reach = 4.5d; // Fallback to default reach

            // Get the player's eye position and view direction
            net.minecraft.world.phys.Vec3 eyePos = player.getEyePosition(partialTicks);
            net.minecraft.world.phys.Vec3 viewVec = player.getViewVector(partialTicks);
            net.minecraft.world.phys.Vec3 endPos = eyePos.add(viewVec.x * reach, viewVec.y * reach, viewVec.z * reach);

            // Create a search box along the view vector
            net.minecraft.world.phys.AABB searchBox = player.getBoundingBox().expandTowards(viewVec.scale(reach)).inflate(1.0D, 1.0D, 1.0D);

            // Use ProjectileUtil to raycast and find the closest ItemEntity
            return net.minecraft.world.entity.projectile.ProjectileUtil.getEntityHitResult(
                    player,
                    eyePos,
                    endPos,
                    searchBox,
                    (entity) -> entity instanceof net.minecraft.world.entity.item.ItemEntity, // Only look for items
                    reach * reach  // Max distance squared
            );
        }
    }
}
