package com.tacz.guns.network.message.event;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
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

public record ServerMessageGunHurt(int bulletId, int hurtEntityId, int attackerId, ResourceLocation gunId, ResourceLocation gunDisplayId,
                                   float amount, boolean isHeadShot, float headshotMultiplier) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerMessageGunHurt> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "s2c_gun_hurt"));
    public static final StreamCodec<FriendlyByteBuf, ServerMessageGunHurt> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ServerMessageGunHurt decode(FriendlyByteBuf buf) {
            int bulletId = buf.readInt();
            int hurtEntityId = buf.readInt();
            int attackerId = buf.readInt();
            ResourceLocation gunId = buf.readResourceLocation();
            ResourceLocation gunDisplayId = buf.readResourceLocation();
            float amount = buf.readFloat();
            boolean isHeadShot = buf.readBoolean();
            float headshotMultiplier = buf.readFloat();
            return new ServerMessageGunHurt(bulletId, hurtEntityId, attackerId, gunId, gunDisplayId, amount, isHeadShot, headshotMultiplier);
        }
        @Override
        public void encode(FriendlyByteBuf buf, ServerMessageGunHurt message) {
            buf.writeInt(message.bulletId);
            buf.writeInt(message.hurtEntityId);
            buf.writeInt(message.attackerId);
            buf.writeResourceLocation(message.gunId);
            buf.writeResourceLocation(message.gunDisplayId);
            buf.writeFloat(message.amount);
            buf.writeBoolean(message.isHeadShot);
            buf.writeFloat(message.headshotMultiplier);
        }
    };

    public static void clientHandler(final ServerMessageGunHurt message, final IPayloadContext context) {
        context.enqueueWork(() -> onHurt(message));
    }

    public static void serverHandler(final ServerMessageGunHurt message, final IPayloadContext context) {
    }

    @OnlyIn(Dist.CLIENT)
    private static void onHurt(ServerMessageGunHurt message) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        @Nullable Entity bullet = level.getEntity(message.bulletId);
        @Nullable Entity hurtEntity = level.getEntity(message.hurtEntityId);
        @Nullable LivingEntity attacker = level.getEntity(message.attackerId) instanceof LivingEntity livingEntity ? livingEntity : null;
        NeoForge.EVENT_BUS.post(new EntityHurtByGunEvent.Post(bullet, hurtEntity, attacker, message.gunId, message.gunDisplayId, message.amount, null, message.isHeadShot, message.headshotMultiplier, LogicalSide.CLIENT));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
