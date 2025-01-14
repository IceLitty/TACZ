package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.IGunOperator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientMessagePlayerMelee() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientMessagePlayerMelee> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "c2s_player_melee"));
    public static final StreamCodec<FriendlyByteBuf, ClientMessagePlayerMelee> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ClientMessagePlayerMelee decode(FriendlyByteBuf buf) {
            return new ClientMessagePlayerMelee();
        }
        @Override
        public void encode(FriendlyByteBuf buf, ClientMessagePlayerMelee message) {
        }
    };

    public static void clientHandler(final ClientMessagePlayerMelee message, final IPayloadContext context) {
    }

    public static void serverHandler(final ClientMessagePlayerMelee message, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer entity) {
                IGunOperator.fromLivingEntity(entity).melee();
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
