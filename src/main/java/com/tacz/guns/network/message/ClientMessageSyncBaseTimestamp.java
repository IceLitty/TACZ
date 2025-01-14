package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public record ClientMessageSyncBaseTimestamp() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientMessageSyncBaseTimestamp> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "c2s_sync_base_timestamp"));
    public static final StreamCodec<FriendlyByteBuf, ClientMessageSyncBaseTimestamp> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ClientMessageSyncBaseTimestamp decode(FriendlyByteBuf buf) {
            return new ClientMessageSyncBaseTimestamp();
        }
        @Override
        public void encode(FriendlyByteBuf buf, ClientMessageSyncBaseTimestamp message) {
        }
    };
    private static final Marker MARKER = MarkerManager.getMarker("SYNC_BASE_TIMESTAMP");

    public static void clientHandler(final ClientMessageSyncBaseTimestamp message, final IPayloadContext context) {
    }

    public static void serverHandler(final ClientMessageSyncBaseTimestamp message, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer entity) {
                long timestamp = System.currentTimeMillis();
                ShooterDataHolder dataHolder = IGunOperator.fromLivingEntity(entity).getDataHolder();
                dataHolder.baseTimestamp = timestamp;
                GunMod.LOGGER.debug(MARKER, "Update server base timestamp: {}", dataHolder.baseTimestamp);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
