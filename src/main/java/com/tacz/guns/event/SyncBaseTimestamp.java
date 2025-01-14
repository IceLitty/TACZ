package com.tacz.guns.event;

import com.tacz.guns.GunMod;
import com.tacz.guns.network.message.ServerMessageSyncBaseTimestamp;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = GunMod.MOD_ID)
public class SyncBaseTimestamp {
    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof ServerPlayer serverPlayer && !event.getLevel().isClientSide()) {
            PacketDistributor.sendToPlayer(serverPlayer, new ServerMessageSyncBaseTimestamp());
        }
    }
}
