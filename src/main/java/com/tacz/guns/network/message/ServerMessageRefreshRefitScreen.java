package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.client.gui.GunRefitScreen;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerMessageRefreshRefitScreen() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerMessageRefreshRefitScreen> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "s2c_refresh_refit_screen"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerMessageRefreshRefitScreen> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ServerMessageRefreshRefitScreen decode(RegistryFriendlyByteBuf pBuffer) {
            return new ServerMessageRefreshRefitScreen();
        }
        @Override
        public void encode(RegistryFriendlyByteBuf pBuffer, ServerMessageRefreshRefitScreen pValue) {
        }
    };

    public static void clientHandler(final ServerMessageRefreshRefitScreen message, final IPayloadContext context) {
        context.enqueueWork(ServerMessageRefreshRefitScreen::updateScreen);
    }

    public static void serverHandler(final ServerMessageRefreshRefitScreen message, final IPayloadContext context) {
    }

    @OnlyIn(Dist.CLIENT)
    private static void updateScreen() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && Minecraft.getInstance().screen instanceof GunRefitScreen screen) {
            screen.init();
            // 刷新配件数据，客户端的
            AttachmentPropertyManager.postChangeEvent(player, player.getMainHandItem());
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
