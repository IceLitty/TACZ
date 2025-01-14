package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.IGunOperator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientMessagePlayerCancelReload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientMessagePlayerCancelReload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "c2s_player_cancel_reload"));
    public static final StreamCodec<FriendlyByteBuf, ClientMessagePlayerCancelReload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ClientMessagePlayerCancelReload decode(FriendlyByteBuf buf) {
            return new ClientMessagePlayerCancelReload();
        }
        @Override
        public void encode(FriendlyByteBuf buf, ClientMessagePlayerCancelReload message) {
        }
    };

    public static void clientHandler(final ClientMessagePlayerCancelReload message, final IPayloadContext context) {
    }

    public static void serverHandler(final ClientMessagePlayerCancelReload message, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer entity) {
                IGunOperator.fromLivingEntity(entity).cancelReload();
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
