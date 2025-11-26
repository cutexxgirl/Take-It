package com.cutexxgirl.takeit.events;

import com.cutexxgirl.takeit.TakeIt;
import com.cutexxgirl.takeit.TakeItConfig;
import com.cutexxgirl.takeit.network.PacketHandler;
import com.cutexxgirl.takeit.network.PacketPickup;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
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

    /**
     * Register the keybinding
     * Called during mod initialization
     * 
     * @param event The key registration event
     */
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(PICKUP_KEY);
    }

    /**
     * ForgeEvents - Event handlers that run every tick/frame
     * These are registered on the Forge event bus (not mod bus)
     */
    @Mod.EventBusSubscriber(modid = TakeIt.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        // Tracks whether right mouse button was pressed last tick
        // Used to detect the moment when button is first pressed (not held)
        private static boolean wasRightClickPressed = false;

        /**
         * Handle keyboard input
         * Detects when the pickup key (G) is pressed
         * 
         * @param event The key input event
         */
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            if (PICKUP_KEY.consumeClick() && TakeItConfig.ENABLE_MOD.get()) {
                // Only trigger if no GUI is open (prevents pickup while in inventory)
                if (net.minecraft.client.Minecraft.getInstance().screen == null) {
                    // Send packet to server to trigger radius pickup
                    PacketHandler.INSTANCE.sendToServer(new PacketPickup());
                }
            }
        }

        /**
         * Handle client tick - runs every game tick (20 times per second)
         * Used to detect right-click on items
         * 
         * Why not use mouse input event?
         * - Mouse input events don't fire reliably for item entities
         * - Tick-based detection is more reliable for custom raycasting
         * 
         * @param event The client tick event
         */
        @SubscribeEvent
        public static void onClientTick(net.minecraftforge.event.TickEvent.ClientTickEvent event) {
            if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) return;
            if (!TakeItConfig.ENABLE_MOD.get()) return;

            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.player == null || mc.screen != null) return;

            // Check if right mouse button is currently pressed
            boolean isRightClickPressed = mc.options.keyUse.isDown();

            // Only trigger on the rising edge (when button is first pressed, not while held)
            if (isRightClickPressed && !wasRightClickPressed) {
                // Vanilla hitResult ignores ItemEntity, so we must raycast manually
                net.minecraft.world.phys.HitResult hit = pick(mc.player, 20.0F);
                
                if (hit != null && hit.getType() == net.minecraft.world.phys.HitResult.Type.ENTITY) {
                    net.minecraft.world.phys.EntityHitResult entityHit = (net.minecraft.world.phys.EntityHitResult) hit;
                    if (entityHit.getEntity() instanceof net.minecraft.world.entity.item.ItemEntity) {
                        // Send packet to server to pick up this specific item
                        PacketHandler.INSTANCE.sendToServer(new com.cutexxgirl.takeit.network.PacketClickPickup(entityHit.getEntity().getId()));
                        com.mojang.logging.LogUtils.getLogger().info("TakeIt: Sent click pickup packet for entity ID: {}", entityHit.getEntity().getId());
                    }
                }
            }

            wasRightClickPressed = isRightClickPressed;
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
}
