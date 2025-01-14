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

public record ClientMessagePlayerAim(boolean isAim) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientMessagePlayerAim> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "c2s_player_aim"));
    public static final StreamCodec<FriendlyByteBuf, ClientMessagePlayerAim> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ClientMessagePlayerAim::isAim,
            ClientMessagePlayerAim::new
    );

    public static void clientHandler(final ClientMessagePlayerAim message, final IPayloadContext context) {
    }

    public static void serverHandler(final ClientMessagePlayerAim message, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer entity) {
                IGunOperator.fromLivingEntity(entity).aim(message.isAim);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
