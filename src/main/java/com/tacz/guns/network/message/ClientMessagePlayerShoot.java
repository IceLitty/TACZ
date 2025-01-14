package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.IGunOperator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 这里的 timestamp 应该是基于 base timestamp 的相对值
 */
public record ClientMessagePlayerShoot(long timestamp) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientMessagePlayerShoot> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "c2s_player_shoot"));
    public static final StreamCodec<FriendlyByteBuf, ClientMessagePlayerShoot> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, ClientMessagePlayerShoot::timestamp,
            ClientMessagePlayerShoot::new
    );

    public static void clientHandler(final ClientMessagePlayerShoot message, final IPayloadContext context) {
    }

    public static void serverHandler(final ClientMessagePlayerShoot message, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer entity) {
                IGunOperator.fromLivingEntity(entity).shoot(entity::getXRot, entity::getYRot, message.timestamp);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
