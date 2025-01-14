package com.tacz.guns.api.item.nbt;

import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.util.helper.NBTHelper;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public interface AttachmentItemDataAccessor extends IAttachment {
    String ATTACHMENT_ID_TAG = "AttachmentId";
    String SKIN_ID_TAG = "Skin";
    String ZOOM_NUMBER_TAG = "ZoomNumber";

    // 仅检查给定的 CompoundTag 是否具有配件 ID ，不校验其是否存在
    static boolean isAttachmentLike(CompoundTag tag) {
        return tag.contains(ATTACHMENT_ID_TAG, Tag.TAG_STRING);
    }

    /**
     * Same as {@link AttachmentItemDataAccessor#isAttachmentLike(CompoundTag)}
     */
    static boolean isAttachmentLike(ItemStack itemStack) {
        CompoundTag baseTag = NBTHelper.getCustomTagFromItemStackReadonly(itemStack);
        if (baseTag != null) {
            return baseTag.contains(ATTACHMENT_ID_TAG);
        }
        return false;
    }

    @Nonnull
    static ResourceLocation getAttachmentIdFromTag(@Nullable CompoundTag nbt) {
        if (nbt == null) {
            return DefaultAssets.EMPTY_ATTACHMENT_ID;
        }
        if (isAttachmentLike(nbt)) {
            ResourceLocation attachmentId = ResourceLocation.tryParse(nbt.getString(ATTACHMENT_ID_TAG));
            return Objects.requireNonNullElse(attachmentId, DefaultAssets.EMPTY_ATTACHMENT_ID);
        }
        return DefaultAssets.EMPTY_ATTACHMENT_ID;
    }

    /**
     * Same as {@link AttachmentItemDataAccessor#getAttachmentIdFromTag(CompoundTag)}
     */
    @Nonnull
    static ResourceLocation getAttachmentIdFromItemStack(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return DefaultAssets.EMPTY_ATTACHMENT_ID;
        }
        if (isAttachmentLike(itemStack)) {
            CompoundTag baseTag = NBTHelper.getCustomTagFromItemStackReadonly(itemStack);
            ResourceLocation attachmentId = ResourceLocation.tryParse(baseTag.getString(ATTACHMENT_ID_TAG));
            return Objects.requireNonNullElse(attachmentId, DefaultAssets.EMPTY_ATTACHMENT_ID);
        }
        return DefaultAssets.EMPTY_ATTACHMENT_ID;
    }

    static int getZoomNumberFromTag(@Nullable CompoundTag nbt) {
        if (nbt == null) {
            return 0;
        }
        if (nbt.contains(ZOOM_NUMBER_TAG, Tag.TAG_INT)) {
            return nbt.getInt(ZOOM_NUMBER_TAG);
        }
        return 0;
    }

    /**
     * NEED MANUALLY WRITE BACK TO ITEM STACK
     */
    static void setZoomNumberToTag(CompoundTag nbt, int zoomNumber) {
        nbt.putInt(ZOOM_NUMBER_TAG, zoomNumber);
    }

    @Override
    @Nonnull
    default ResourceLocation getAttachmentId(ItemStack attachmentStack) {
        CompoundTag nbt = NBTHelper.getCustomTagFromItemStackReadonly(attachmentStack);
        return getAttachmentIdFromTag(nbt);
    }

    @Override
    default void setAttachmentId(ItemStack attachmentStack, @Nullable ResourceLocation attachmentId) {
        if (attachmentId != null) {
            NBTHelper.setCustomTagToItemStack(attachmentStack, t -> t.putString(ATTACHMENT_ID_TAG, attachmentId.toString()));
        }
    }

    @Override
    @Nullable
    default ResourceLocation getSkinId(ItemStack attachmentStack) {
        String resourceId = NBTHelper.getTagValueFromItemStack(attachmentStack, SKIN_ID_TAG, (String) null);
        if (resourceId != null) {
            return ResourceLocation.tryParse(resourceId);
        }
        return null;
    }

    @Override
    default void setSkinId(ItemStack attachmentStack, @Nullable ResourceLocation skinId) {
        if (skinId != null) {
            NBTHelper.setCustomTagToItemStack(attachmentStack, t -> t.putString(SKIN_ID_TAG, skinId.toString()));
        } else {
            NBTHelper.removeCustomTagToItemStack(attachmentStack, SKIN_ID_TAG);
        }
    }

    @Override
    default int getZoomNumber(ItemStack attachmentStack) {
        CompoundTag nbt = NBTHelper.getCustomTagFromItemStackReadonly(attachmentStack);
        return getZoomNumberFromTag(nbt);
    }

    @Override
    default void setZoomNumber(ItemStack attachmentStack, int zoomNumber) {
        NBTHelper.setCustomTagToItemStack(attachmentStack, t -> setZoomNumberToTag(t, zoomNumber));
    }
}
