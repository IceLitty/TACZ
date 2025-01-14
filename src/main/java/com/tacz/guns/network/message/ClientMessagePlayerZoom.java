package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.IGunOperator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientMessagePlayerZoom() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientMessagePlayerZoom> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "c2s_player_zoom"));
    public static final StreamCodec<FriendlyByteBuf, ClientMessagePlayerZoom> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ClientMessagePlayerZoom decode(FriendlyByteBuf buf) {
            return new ClientMessagePlayerZoom();
        }
        @Override
        public void encode(FriendlyByteBuf buf, ClientMessagePlayerZoom message) {
        }
    };

    public static void clientHandler(final ClientMessagePlayerZoom message, final IPayloadContext context) {
    }

    public static void serverHandler(final ClientMessagePlayerZoom message, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer entity) {
                IGunOperator.fromLivingEntity(entity).zoom();
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
