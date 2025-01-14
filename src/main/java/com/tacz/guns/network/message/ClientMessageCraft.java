package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.inventory.GunSmithTableMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientMessageCraft(ResourceLocation recipeId, int menuId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientMessageCraft> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "c2s_craft"));
    public static final StreamCodec<FriendlyByteBuf, ClientMessageCraft> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, ClientMessageCraft::recipeId,
            ByteBufCodecs.VAR_INT, ClientMessageCraft::menuId,
            ClientMessageCraft::new
    );

    public static void clientHandler(final ClientMessageCraft message, final IPayloadContext context) {
    }

    public static void serverHandler(final ClientMessageCraft message, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer entity) {
                if (entity.containerMenu.containerId == message.menuId && entity.containerMenu instanceof GunSmithTableMenu menu) {
                    menu.doCraft(message.recipeId, entity);
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
