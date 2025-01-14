package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.IGunOperator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientMessagePlayerReloadGun() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientMessagePlayerReloadGun> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "c2s_player_reload_gun"));
    public static final StreamCodec<FriendlyByteBuf, ClientMessagePlayerReloadGun> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ClientMessagePlayerReloadGun decode(FriendlyByteBuf buf) {
            return new ClientMessagePlayerReloadGun();
        }
        @Override
        public void encode(FriendlyByteBuf buf, ClientMessagePlayerReloadGun message) {
        }
    };

    public static void clientHandler(final ClientMessagePlayerReloadGun message, final IPayloadContext context) {
    }

    public static void serverHandler(final ClientMessagePlayerReloadGun message, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer entity) {
                IGunOperator.fromLivingEntity(entity).reload();
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
