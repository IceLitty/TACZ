package com.tacz.guns.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public class NetworkHandler {

    public static void sendToClientPlayer(CustomPacketPayload message, Player player) {
        PacketDistributor.sendToPlayer((ServerPlayer) player, message);
    }

    /**
     * 发送给所有监听此实体的玩家
     */
    public static void sendToTrackingEntityAndSelf(Entity centerEntity, CustomPacketPayload message) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(centerEntity, message);
    }

    public static void sendToAllPlayers(CustomPacketPayload message) {
        PacketDistributor.sendToAllPlayers(message);
    }

    public static void sendToTrackingEntity(CustomPacketPayload message, final Entity centerEntity) {
        PacketDistributor.sendToPlayersTrackingEntity(centerEntity, message);
    }

    public static void sendToDimension(CustomPacketPayload message, final Entity centerEntity) {
        ServerLevel level = centerEntity.level().getServer().getLevel(centerEntity.level().dimension());
        PacketDistributor.sendToPlayersInDimension(level, message);
    }
}
