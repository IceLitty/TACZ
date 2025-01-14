package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.client.gameplay.LocalPlayerDataHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Objects;

public record ServerMessageSyncBaseTimestamp() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerMessageSyncBaseTimestamp> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "s2c_sync_base_timestamp"));
    public static final StreamCodec<FriendlyByteBuf, ServerMessageSyncBaseTimestamp> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ServerMessageSyncBaseTimestamp decode(FriendlyByteBuf pBuffer) {
            return new ServerMessageSyncBaseTimestamp();
        }
        @Override
        public void encode(FriendlyByteBuf pBuffer, ServerMessageSyncBaseTimestamp pValue) {
        }
    };
    private static final Marker MARKER = MarkerManager.getMarker("SYNC_BASE_TIMESTAMP");

    public static void clientHandler(final ServerMessageSyncBaseTimestamp message, final IPayloadContext context) {
        long timestamp = System.currentTimeMillis();
        context.enqueueWork(() -> updateBaseTimestamp(timestamp));
        context.reply(new ServerMessageSyncBaseTimestamp());
    }

    public static void serverHandler(final ServerMessageSyncBaseTimestamp message, final IPayloadContext context) {
        context.reply(new ServerMessageSyncBaseTimestamp());
    }

    @OnlyIn(Dist.CLIENT)
    private static void updateBaseTimestamp(long timestamp) {
        LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);
        LocalPlayerDataHolder dataHolder = IClientPlayerGunOperator.fromLocalPlayer(player).getDataHolder();
        dataHolder.clientBaseTimestamp = timestamp;
//        GunMod.LOGGER.debug(MARKER, "Update client base timestamp: {}", dataHolder.clientBaseTimestamp);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
