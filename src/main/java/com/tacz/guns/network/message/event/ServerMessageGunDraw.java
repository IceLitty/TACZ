package com.tacz.guns.network.message.event;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.event.common.GunDrawEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerMessageGunDraw(int entityId, ItemStack previousGunItem, ItemStack currentGunItem) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerMessageGunDraw> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "s2c_gun_draw"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerMessageGunDraw> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ServerMessageGunDraw::entityId,
            ItemStack.STREAM_CODEC, ServerMessageGunDraw::previousGunItem,
            ItemStack.STREAM_CODEC, ServerMessageGunDraw::currentGunItem,
            ServerMessageGunDraw::new
    );

    public static void clientHandler(final ServerMessageGunDraw message, final IPayloadContext context) {
        context.enqueueWork(() -> doClientEvent(message));
    }

    public static void serverHandler(final ServerMessageGunDraw message, final IPayloadContext context) {
    }

    @OnlyIn(Dist.CLIENT)
    private static void doClientEvent(ServerMessageGunDraw message) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        if (level.getEntity(message.entityId) instanceof LivingEntity livingEntity) {
            GunDrawEvent gunDrawEvent = new GunDrawEvent(livingEntity, message.previousGunItem, message.currentGunItem, LogicalSide.CLIENT);
            NeoForge.EVENT_BUS.post(gunDrawEvent);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
