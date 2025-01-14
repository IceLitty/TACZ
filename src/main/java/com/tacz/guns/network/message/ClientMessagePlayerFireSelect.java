package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.IGunOperator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientMessagePlayerFireSelect() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientMessagePlayerFireSelect> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "c2s_player_fire_select"));
    public static final StreamCodec<FriendlyByteBuf, ClientMessagePlayerFireSelect> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ClientMessagePlayerFireSelect decode(FriendlyByteBuf buf) {
            return new ClientMessagePlayerFireSelect();
        }
        @Override
        public void encode(FriendlyByteBuf buf, ClientMessagePlayerFireSelect message) {
        }
    };

    public static void clientHandler(final ClientMessagePlayerFireSelect message, final IPayloadContext context) {
    }

    public static void serverHandler(final ClientMessagePlayerFireSelect message, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer entity) {
                IGunOperator.fromLivingEntity(entity).fireSelect();
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
