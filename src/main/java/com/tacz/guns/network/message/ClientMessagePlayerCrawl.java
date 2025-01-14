package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.config.sync.SyncConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientMessagePlayerCrawl(boolean isCrawl) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientMessagePlayerCrawl> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "c2s_player_crawl"));
    public static final StreamCodec<FriendlyByteBuf, ClientMessagePlayerCrawl> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ClientMessagePlayerCrawl::isCrawl,
            ClientMessagePlayerCrawl::new
    );

    public static void clientHandler(final ClientMessagePlayerCrawl message, final IPayloadContext context) {
    }

    public static void serverHandler(final ClientMessagePlayerCrawl message, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer entity) {
                if (!SyncConfig.ENABLE_CRAWL.get()) {
                    return;
                }
                IGunOperator.fromLivingEntity(entity).crawl(message.isCrawl);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
