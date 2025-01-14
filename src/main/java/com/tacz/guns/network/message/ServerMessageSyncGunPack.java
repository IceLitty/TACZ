package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.client.resource.ClientIndexManager;
import com.tacz.guns.resource.network.CommonNetworkCache;
import com.tacz.guns.resource.network.DataType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Map;

public record ServerMessageSyncGunPack(Map<DataType, Map<ResourceLocation, String>> cache) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerMessageSyncGunPack> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "s2c_sync_gun_pack"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerMessageSyncGunPack> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ServerMessageSyncGunPack decode(RegistryFriendlyByteBuf buf) {
            Map<DataType, Map<ResourceLocation, String>> map = buf.readMap(buf1 -> buf1.readEnum(DataType.class), buf2 -> {
                return buf2.readMap(FriendlyByteBuf::readResourceLocation, ByteBufCodecs.STRING_UTF8);
            });
            return new ServerMessageSyncGunPack(map);
        }
        @Override
        public void encode(RegistryFriendlyByteBuf buf, ServerMessageSyncGunPack message) {
            buf.writeMap(message.getCache(), FriendlyByteBuf::writeEnum, (buf1, map) -> {
                buf1.writeMap(map, ResourceLocation.STREAM_CODEC, ByteBufCodecs.STRING_UTF8);
            });
        }
    };

    public static void clientHandler(final ServerMessageSyncGunPack message, final IPayloadContext context) {
        context.enqueueWork(() -> doSync(message));
    }

    public static void serverHandler(final ServerMessageSyncGunPack message, final IPayloadContext context) {
    }

    public Map<DataType, Map<ResourceLocation, String>> getCache() {
        return cache;
    }

    @OnlyIn(Dist.CLIENT)
    private static void doSync(ServerMessageSyncGunPack message) {
        CommonNetworkCache.INSTANCE.fromNetwork(message.cache);
        // 通知客户端重新构建ClientIndex
        ClientIndexManager.reload();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
