package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientMessageRefitGun(int attachmentSlotIndex, int gunSlotIndex, AttachmentType attachmentType) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientMessageRefitGun> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "c2s_refit_gun"));
    public static final StreamCodec<FriendlyByteBuf, ClientMessageRefitGun> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientMessageRefitGun::attachmentSlotIndex,
            ByteBufCodecs.VAR_INT, ClientMessageRefitGun::gunSlotIndex,
            AttachmentType.ID_STREAM_CODEC, ClientMessageRefitGun::attachmentType,
            ClientMessageRefitGun::new
    );

    public static void clientHandler(final ClientMessageRefitGun message, final IPayloadContext context) {
    }

    public static void serverHandler(final ClientMessageRefitGun message, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                Inventory inventory = player.getInventory();
                ItemStack attachmentItem = inventory.getItem(message.attachmentSlotIndex);
                ItemStack gunItem = inventory.getItem(message.gunSlotIndex);
                IGun iGun = IGun.getIGunOrNull(gunItem);
                if (iGun != null) {
                    if (iGun.allowAttachment(gunItem, attachmentItem)) {
                        ItemStack oldAttachmentItem = iGun.getAttachment(gunItem, message.attachmentType);
                        iGun.installAttachment(gunItem, attachmentItem);
                        // 刷新配件数据
                        AttachmentPropertyManager.postChangeEvent(player, gunItem);
                        inventory.setItem(message.attachmentSlotIndex, oldAttachmentItem);
                        // 如果卸载的是扩容弹匣，吐出所有子弹
                        if (message.attachmentType == AttachmentType.EXTENDED_MAG) {
                            iGun.dropAllAmmo(player, gunItem);
                        }
                        player.inventoryMenu.broadcastChanges();
                        NetworkHandler.sendToClientPlayer(new ServerMessageRefreshRefitScreen(), player);
                    }
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
