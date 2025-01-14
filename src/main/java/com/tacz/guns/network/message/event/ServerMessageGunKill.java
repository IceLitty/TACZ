package com.tacz.guns.network.message.event;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nullable;

public record ServerMessageGunKill(int bulletId, int killEntityId, int attackerId, ResourceLocation gunId, ResourceLocation gunDisplayId,
                                   float baseDamage, boolean isHeadShot, float headshotMultiplier) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerMessageGunKill> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "s2c_gun_kill"));
    public static final StreamCodec<FriendlyByteBuf, ServerMessageGunKill> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ServerMessageGunKill decode(FriendlyByteBuf buf) {
            int bulletId = buf.readInt();
            int killEntityId = buf.readInt();
            int attackerId = buf.readInt();
            ResourceLocation gunId = buf.readResourceLocation();
            ResourceLocation gunDisplayId = buf.readResourceLocation();
            float baseDamage = buf.readFloat();
            boolean isHeadShot = buf.readBoolean();
            float headshotMultiplier = buf.readFloat();
            return new ServerMessageGunKill(bulletId, killEntityId, attackerId, gunId, gunDisplayId, baseDamage, isHeadShot, headshotMultiplier);
        }
        @Override
        public void encode(FriendlyByteBuf buf, ServerMessageGunKill message) {
            buf.writeInt(message.bulletId);
            buf.writeInt(message.killEntityId);
            buf.writeInt(message.attackerId);
            buf.writeResourceLocation(message.gunId);
            buf.writeResourceLocation(message.gunDisplayId);
            buf.writeFloat(message.baseDamage);
            buf.writeBoolean(message.isHeadShot);
            buf.writeFloat(message.headshotMultiplier);

        }
    };

    public static void clientHandler(final ServerMessageGunKill message, final IPayloadContext context) {
        context.enqueueWork(() -> onKill(message));
    }

    public static void serverHandler(final ServerMessageGunKill message, final IPayloadContext context) {
    }

    @OnlyIn(Dist.CLIENT)
    private static void onKill(ServerMessageGunKill message) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        @Nullable Entity bullet = level.getEntity(message.bulletId);
        @Nullable LivingEntity killedEntity = level.getEntity(message.killEntityId) instanceof LivingEntity livingEntity ? livingEntity : null;
        @Nullable LivingEntity attacker = level.getEntity(message.attackerId) instanceof LivingEntity livingEntity ? livingEntity : null;
        NeoForge.EVENT_BUS.post(new EntityKillByGunEvent(bullet, killedEntity, attacker, message.gunId, message.gunDisplayId, message.baseDamage, null, message.isHeadShot, message.headshotMultiplier, LogicalSide.CLIENT));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
