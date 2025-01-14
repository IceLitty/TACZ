package com.tacz.guns.event;

import com.tacz.guns.GunMod;
import com.tacz.guns.entity.sync.core.*;
import com.tacz.guns.network.message.ServerMessageUpdateEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
//import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = GunMod.MOD_ID)
public final class SyncedEntityDataEvent {
//    @SubscribeEvent
//    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
//        if (SyncedEntityData.instance().hasSyncedDataKey(event.getObject())) {
//            DataHolderCapabilityProvider provider = new DataHolderCapabilityProvider();
//            event.addCapability(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "synced_entity_data"), provider);
//            // Don't add invalidate to server player since it's persistent
//            if (!(event.getObject() instanceof ServerPlayer)) {
//                event.addListener(provider::invalidate);
//            }
//        }
//    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (!event.getEntity().level().isClientSide()) {
            Entity entity = event.getTarget();
            DataHolder holder = SyncedEntityData.instance().getDataHolder(entity);
            if (holder != null) {
                List<DataEntry<?, ?>> entries = holder.gatherAllTrackingDataEntries();
                entries.removeIf(entry -> !entry.getKey().syncMode().isTracking());
                if (!entries.isEmpty()) {
                    PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(), new ServerMessageUpdateEntityData(entity.getId(), entries));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player && !event.getLevel().isClientSide()) {
            DataHolder holder = SyncedEntityData.instance().getDataHolder(player);
            if (holder != null) {
                List<DataEntry<?, ?>> entries = holder.gatherAllTrackingDataEntries();
                if (!entries.isEmpty()) {
                    PacketDistributor.sendToPlayer((ServerPlayer) player, new ServerMessageUpdateEntityData(player.getId(), entries));
                }
            }
        }
    }

//    @SubscribeEvent
//    public static void onPlayerClone(PlayerEvent.Clone event) {
//        Player original = event.getOriginal();
//        original.reviveCaps();
//        DataHolder oldHolder = SyncedEntityData.instance().getDataHolder(original);
//        if (oldHolder == null) {
//            return;
//        }
//        original.invalidateCaps();
//        Player player = event.getEntity();
//        DataHolder newHolder = SyncedEntityData.instance().getDataHolder(player);
//        if (newHolder == null) {
//            return;
//        }
//        Map<SyncedDataKey<?, ?>, DataEntry<?, ?>> dataMap = new HashMap<>(oldHolder.dataMap);
//        if (event.isWasDeath()) {
//            dataMap.entrySet().removeIf(entry -> !entry.getKey().persistent());
//        }
//        newHolder.dataMap = dataMap;
//    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        SyncedEntityData instance = SyncedEntityData.instance();
        if (!instance.isDirty()) {
            return;
        }
        Set<Entity> dirtyEntities = instance.getDirtyEntities();
        if (dirtyEntities.isEmpty()) {
            instance.setDirty(false);
            return;
        }
        for (Entity entity : dirtyEntities) {
            DataHolder holder = instance.getDataHolder(entity);
            if (holder == null || !holder.isPendingSync()) {
                continue;
            }
            List<DataEntry<?, ?>> entries = holder.gatherPendingSyncDataEntries();
            if (entries.isEmpty()) {
                continue;
            }
            List<DataEntry<?, ?>> selfEntries = entries.stream().filter(entry -> entry.getKey().syncMode().isSelf()).collect(Collectors.toList());
            if (!selfEntries.isEmpty() && entity instanceof ServerPlayer) {
                PacketDistributor.sendToPlayer((ServerPlayer) entity, new ServerMessageUpdateEntityData(entity.getId(), selfEntries));
            }
            List<DataEntry<?, ?>> trackingEntries = entries.stream().filter(entry -> entry.getKey().syncMode().isTracking()).collect(Collectors.toList());
            if (!trackingEntries.isEmpty()) {
                PacketDistributor.sendToPlayersTrackingEntity(entity, new ServerMessageUpdateEntityData(entity.getId(), trackingEntries));
            }
            holder.clearSync();
        }
        dirtyEntities.clear();
        instance.setDirty(false);
    }
}