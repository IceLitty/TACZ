package com.tacz.guns.network.message.handshake;

import com.tacz.guns.GunMod;
import com.tacz.guns.network.LoginIndexHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class Acknowledge extends LoginIndexHolder implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<Acknowledge> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "c2s_sync_entity_data_mapping"));
    public static final StreamCodec<FriendlyByteBuf, Acknowledge> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public Acknowledge decode(FriendlyByteBuf buf) {
            return new Acknowledge();
        }
        @Override
        public void encode(FriendlyByteBuf buf, Acknowledge message) {
        }
    };
    public static final Marker ACKNOWLEDGE = MarkerManager.getMarker("HANDSHAKE_ACKNOWLEDGE");

    public static void handle(final Acknowledge message, final IPayloadContext context) {
        GunMod.LOGGER.debug(ACKNOWLEDGE, "Received acknowledgement from client");
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
