package com.tacz.guns.network.message.event;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.event.common.GunMeleeEvent;
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

public record ServerMessageGunMelee(int shooterId, ItemStack gunItemStack) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerMessageGunMelee> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "s2c_gun_melee"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerMessageGunMelee> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ServerMessageGunMelee::shooterId,
            ItemStack.STREAM_CODEC, ServerMessageGunMelee::gunItemStack,
            ServerMessageGunMelee::new
    );

    public static void clientHandler(final ServerMessageGunMelee message, final IPayloadContext context) {
        context.enqueueWork(() -> doClientEvent(message));
    }

    public static void serverHandler(final ServerMessageGunMelee message, final IPayloadContext context) {
    }

    @OnlyIn(Dist.CLIENT)
    private static void doClientEvent(ServerMessageGunMelee message) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        if (level.getEntity(message.shooterId) instanceof LivingEntity shooter) {
            GunMeleeEvent gunMeleeEvent = new GunMeleeEvent(shooter, message.gunItemStack, LogicalSide.CLIENT);
            NeoForge.EVENT_BUS.post(gunMeleeEvent);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
