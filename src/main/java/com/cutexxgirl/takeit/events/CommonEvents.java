package com.cutexxgirl.takeit.events;

import com.cutexxgirl.takeit.TakeIt;
import com.cutexxgirl.takeit.TakeItConfig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * CommonEvents - Server-side and common event handlers
 * 
 * This class handles events that occur on both client and server:
 * - Cancels automatic item pickup when players walk over items
 * - Uses a ThreadLocal flag to allow manual pickup (via click or key press)
 */
@Mod.EventBusSubscriber(modid = TakeIt.MOD_ID)
public class CommonEvents {

    /**
     * ThreadLocal flag to distinguish between manual and automatic pickup
     * 
     * Why ThreadLocal?
     * - Each thread (client/server) has its own copy of this variable
     * - Prevents conflicts in multiplayer scenarios
     * - Automatically cleans up when thread ends
     * 
     * When true: Pickup was triggered by our mod (allow it)
     * When false: Pickup is automatic/vanilla (cancel it)
     */
    public static final ThreadLocal<Boolean> isManualPickup = ThreadLocal.withInitial(() -> false);

    /**
     * Cancel automatic item pickup
     * 
     * This event fires when a player tries to pick up an item.
     * We cancel it UNLESS the pickup was triggered manually by our mod.
     * 
     * @param event The item pickup event
     */
    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        // Only cancel if mod is enabled
        if (!TakeItConfig.ENABLE_MOD.get()) {
            return;
        }

        // If this pickup was triggered by our manual pickup logic, allow it
        if (isManualPickup.get()) {
            return;
        }

        // Otherwise, this is automatic pickup - cancel it!
        event.setCanceled(true);
    }

    
    // We also need to wrap the packet handler logic with the ThreadLocal.
}
