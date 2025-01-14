package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.client.event.SwapItemWithOffHand;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerMessageSwapItem() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerMessageSwapItem> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "s2c_swap_item"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerMessageSwapItem> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ServerMessageSwapItem decode(RegistryFriendlyByteBuf buf) {
            return new ServerMessageSwapItem();
        }
        @Override
        public void encode(RegistryFriendlyByteBuf buf, ServerMessageSwapItem message) {
        }
    };

    public static void clientHandler(final ServerMessageSwapItem message, final IPayloadContext context) {
        NeoForge.EVENT_BUS.post(new SwapItemWithOffHand());
    }

    public static void serverHandler(final ServerMessageSwapItem message, final IPayloadContext context) {
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
