package com.tacz.guns.api.item.nbt;

import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.api.item.builder.AttachmentItemBuilder;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.client.resource.GunDisplayInstance;
import com.tacz.guns.client.resource.index.ClientAttachmentIndex;
import com.tacz.guns.init.ModComponents;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.util.helper.ItemStackHelper;
import com.tacz.guns.util.helper.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

public interface GunItemDataAccessor extends IGun {
    String GUN_ID_TAG = "GunId";
    String GUN_FIRE_MODE_TAG = "GunFireMode";
    String GUN_HAS_BULLET_IN_BARREL = "HasBulletInBarrel";
    String GUN_CURRENT_AMMO_COUNT_TAG = "GunCurrentAmmoCount";
    String GUN_ATTACHMENT_BASE = "Attachment";
    String GUN_EXP_TAG = "GunLevelExp";
    String GUN_DUMMY_AMMO = "DummyAmmo";
    String GUN_MAX_DUMMY_AMMO = "MaxDummyAmmo";
    String GUN_ATTACHMENT_LOCK = "AttachmentLock";

    String GUN_DISPLAY_ID_TAG = "GunDisplayId";

    @Override
    default boolean useDummyAmmo(ItemStack gun) {
        return NBTHelper.getTagValueFromItemStack(gun, GUN_DUMMY_AMMO, (Integer) null) != null;
    }

    @Override
    default int getDummyAmmoAmount(ItemStack gun) {
        return Math.max(0, NBTHelper.getTagValueFromItemStack(gun, GUN_DUMMY_AMMO, 0));
    }

    @Override
    default void setDummyAmmoAmount(ItemStack gun, int amount) {
        NBTHelper.setCustomTagToItemStack(gun, t -> t.putInt(GUN_DUMMY_AMMO, Math.max(amount, 0)));
    }

    @Override
    default void addDummyAmmoAmount(ItemStack gun, int amount) {
        if (!useDummyAmmo(gun)) {
            return;
        }
        int maxDummyAmmo = Integer.MAX_VALUE;
        if (hasMaxDummyAmmo(gun)) {
            maxDummyAmmo = getMaxDummyAmmoAmount(gun);
        }
        amount = Math.min(getDummyAmmoAmount(gun) + amount, maxDummyAmmo);
        int finalAmount = amount;
        NBTHelper.setCustomTagToItemStack(gun, t -> t.putInt(GUN_DUMMY_AMMO, Math.max(finalAmount, 0)));
    }

    @Override
    default boolean hasMaxDummyAmmo(ItemStack gun) {
        Integer val = NBTHelper.getTagValueFromItemStack(gun, GUN_MAX_DUMMY_AMMO, (Integer) null);
        return val != null;
    }

    @Override
    default int getMaxDummyAmmoAmount(ItemStack gun) {
        Integer val = NBTHelper.getTagValueFromItemStack(gun, GUN_MAX_DUMMY_AMMO, 0);
        return Math.max(0, val);
    }

    @Override
    default void setMaxDummyAmmoAmount(ItemStack gun, int amount) {
        NBTHelper.setCustomTagToItemStack(gun, t -> t.putInt(GUN_MAX_DUMMY_AMMO, Math.max(amount, 0)));
    }

    @Override
    default boolean hasAttachmentLock(ItemStack gun) {
        return NBTHelper.getTagValueFromItemStack(gun, GUN_ATTACHMENT_LOCK, false);
    }

    @Override
    default void setAttachmentLock(ItemStack gun, boolean lock) {
        NBTHelper.setCustomTagToItemStack(gun, t -> t.putBoolean(GUN_ATTACHMENT_LOCK, lock));
    }

    @Override
    @Nonnull
    default ResourceLocation getGunId(ItemStack gun) {
        String resourceId = NBTHelper.getTagValueFromItemStack(gun, GUN_ID_TAG, (String) null);
        if (resourceId != null) {
            ResourceLocation gunId = ResourceLocation.tryParse(resourceId);
            return Objects.requireNonNullElse(gunId, DefaultAssets.EMPTY_GUN_ID);
        }
        return DefaultAssets.EMPTY_GUN_ID;
    }

    @Override
    default void setGunId(ItemStack gun, @Nullable ResourceLocation gunId) {
        if (gunId != null)
            NBTHelper.setCustomTagToItemStack(gun, t -> t.putString(GUN_ID_TAG, gunId.toString()));
    }

    @Override
    @NotNull
    default ResourceLocation getGunDisplayId(ItemStack gun) {
        String resourceId = NBTHelper.getTagValueFromItemStack(gun, GUN_DISPLAY_ID_TAG, (String) null);
        if (resourceId != null) {
            ResourceLocation gunDisplayId = ResourceLocation.tryParse(resourceId);
            return Objects.requireNonNullElse(gunDisplayId, DefaultAssets.DEFAULT_GUN_DISPLAY_ID);
        }
        return DefaultAssets.DEFAULT_GUN_DISPLAY_ID;
    }

    @Override
    default void setGunDisplayId(ItemStack gun, ResourceLocation displayId) {
        if (displayId != null)
            NBTHelper.setCustomTagToItemStack(gun, t -> t.putString(GUN_DISPLAY_ID_TAG, displayId.toString()));
    }

    @Override
    default int getLevel(ItemStack gun) {
        Integer val = NBTHelper.getTagValueFromItemStack(gun, GUN_EXP_TAG, (Integer) null);
        if (val == null) {
            return 0;
        }
        return getLevel(val);
    }

    @Override
    default int getExp(ItemStack gun) {
        return NBTHelper.getTagValueFromItemStack(gun, GUN_EXP_TAG, 0);
    }

    @Override
    default int getExpToNextLevel(ItemStack gun) {
        int exp = getExp(gun);
        int level = getLevel(exp);
        if (level >= getMaxLevel()) {
            return 0;
        }
        int nextLevelExp = getExp(level + 1);
        return nextLevelExp - exp;
    }

    @Override
    default int getExpCurrentLevel(ItemStack gun) {
        int exp = getExp(gun);
        int level = getLevel(exp);
        if (level <= 0) {
            return exp;
        } else {
            return exp - getExp(level - 1);
        }
    }

    @Override
    default FireMode getFireMode(ItemStack gun) {
        String val = NBTHelper.getTagValueFromItemStack(gun, GUN_FIRE_MODE_TAG, (String) null);
        if (val != null) {
            return FireMode.valueOf(val);
        }
        return FireMode.UNKNOWN;
    }

    @Override
    default void setFireMode(ItemStack gun, @Nullable FireMode fireMode) {
        if (fireMode != null) {
            NBTHelper.setCustomTagToItemStack(gun, t -> t.putString(GUN_FIRE_MODE_TAG, fireMode.name()));
            return;
        }
        NBTHelper.setCustomTagToItemStack(gun, t -> t.putString(GUN_FIRE_MODE_TAG, FireMode.UNKNOWN.name()));
    }

    @Override
    default int getCurrentAmmoCount(ItemStack gun) {
        return NBTHelper.getTagValueFromItemStack(gun, GUN_CURRENT_AMMO_COUNT_TAG, 0);
    }

    @Override
    default void setCurrentAmmoCount(ItemStack gun, int ammoCount) {
        NBTHelper.setCustomTagToItemStack(gun, t -> t.putInt(GUN_CURRENT_AMMO_COUNT_TAG, Math.max(ammoCount, 0)));
    }

    @Override
    default void reduceCurrentAmmoCount(ItemStack gun) {
        setCurrentAmmoCount(gun, getCurrentAmmoCount(gun) - 1);
    }

    /**
     * READONLY
     */
    @Override
    @Nullable
    default CompoundTag getAttachmentTag(ItemStack gun, AttachmentType type) {
        if (!allowAttachmentType(gun, type)) {
            return null;
        }
        String key = GUN_ATTACHMENT_BASE + type.name();
        CompoundTag baseTag = NBTHelper.getCustomTagFromItemStackReadonly(gun);
        if (baseTag != null && baseTag.contains(key, Tag.TAG_COMPOUND)) {
            CompoundTag allItemStackTag = baseTag.getCompound(key);
            if (allItemStackTag.contains("tag", Tag.TAG_COMPOUND)) {
                return allItemStackTag.getCompound("tag");
            }
        }
        return null;
    }

    @Override
    default void getAttachmentTagAndWrite(ItemStack gun, AttachmentType type, Consumer<CompoundTag> attachmentWriter) {
        if (!allowAttachmentType(gun, type)) {
            return;
        }
        String key = GUN_ATTACHMENT_BASE + type.name();
        CompoundTag baseTag = NBTHelper.getCustomTagFromItemStackReadonly(gun);
        if (baseTag != null && baseTag.contains(key, Tag.TAG_COMPOUND)) {
            CompoundTag allItemStackTag = baseTag.getCompound(key);
            if (allItemStackTag.contains("tag", Tag.TAG_COMPOUND)) {
                attachmentWriter.accept(allItemStackTag.getCompound("tag"));
                gun.set(ModComponents.CUSTOM_DATA, CustomData.of(baseTag));
            }
        }
    }

    @Override
    @NotNull
    default ItemStack getBuiltinAttachment(ItemStack gun, AttachmentType type) {
        IGun iGun = IGun.getIGunOrNull(gun);
        if (iGun == null) {
            return ItemStack.EMPTY;
        }
        CommonGunIndex index = TimelessAPI.getCommonGunIndex(iGun.getGunId(gun)).orElse(null);
        if (index != null){
            var builtin = index.getGunData().getBuiltInAttachments();
            if (builtin.containsKey(type)) {
                return AttachmentItemBuilder.create().setId(builtin.get(type)).build();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    default ItemStack getAttachment(ItemStack gun, AttachmentType type) {
        if (!allowAttachmentType(gun, type)) {
            return ItemStack.EMPTY;
        }
        String key = GUN_ATTACHMENT_BASE + type.name();
        CompoundTag baseTag = NBTHelper.getCustomTagFromItemStackReadonly(gun);
        if (baseTag != null && baseTag.contains(key, Tag.TAG_COMPOUND)) {
            CompoundTag attachment = baseTag.getCompound(key);
            return ItemStackHelper.of(attachment);
        }
        return ItemStack.EMPTY;
    }

    @Override
    @NotNull
    default  ResourceLocation getBuiltInAttachmentId(ItemStack gun, AttachmentType type) {
        IGun iGun = IGun.getIGunOrNull(gun);
        if (iGun == null) {
            return DefaultAssets.EMPTY_ATTACHMENT_ID;
        }
        CommonGunIndex index = TimelessAPI.getCommonGunIndex(iGun.getGunId(gun)).orElse(null);
        if (index != null){
            var builtin = index.getGunData().getBuiltInAttachments();
            if (builtin.containsKey(type)) {
                return builtin.get(type);
            }
        }
        return DefaultAssets.EMPTY_ATTACHMENT_ID;
    }

    @Override
    @Nonnull
    default ResourceLocation getAttachmentId(ItemStack gun, AttachmentType type) {
        CompoundTag attachmentTag = this.getAttachmentTag(gun, type);
        if (attachmentTag != null) {
            return AttachmentItemDataAccessor.getAttachmentIdFromTag(attachmentTag);
        }
        return DefaultAssets.EMPTY_ATTACHMENT_ID;
    }

    @Override
    default void installAttachment(@Nonnull ItemStack gun, @Nonnull ItemStack attachment) {
        if (!allowAttachment(gun, attachment)) {
            return;
        }
        IAttachment iAttachment = IAttachment.getIAttachmentOrNull(attachment);
        if (iAttachment == null) {
            return;
        }
        String key = GUN_ATTACHMENT_BASE + iAttachment.getType(attachment).name();
        CompoundTag attachmentTag = new CompoundTag();
        ItemStackHelper.save(attachment, attachmentTag);
        NBTHelper.setCustomTagToItemStack(gun, t -> t.put(key, attachmentTag));
    }

    @Override
    default void unloadAttachment(@Nonnull ItemStack gun, AttachmentType type) {
        if (!allowAttachmentType(gun, type)) {
            return;
        }
        String key = GUN_ATTACHMENT_BASE + type.name();
        CompoundTag attachmentTag = new CompoundTag();
        ItemStackHelper.save(ItemStack.EMPTY, attachmentTag);
        NBTHelper.setCustomTagToItemStack(gun, t -> t.put(key, attachmentTag));
    }

    @Override
    default float getAimingZoom(ItemStack gunItem) {
        float zoom = 1;
        ResourceLocation scopeId = this.getAttachmentId(gunItem, AttachmentType.SCOPE);
        boolean builtin = false;
        if (scopeId.equals(DefaultAssets.EMPTY_ATTACHMENT_ID)) {
            scopeId = getBuiltInAttachmentId(gunItem, AttachmentType.SCOPE);
            builtin = true;
        }
        if (!DefaultAssets.isEmptyAttachmentId(scopeId)) {
            CompoundTag attachmentTag = this.getAttachmentTag(gunItem, AttachmentType.SCOPE);
            int zoomNumber = builtin ? 0 : AttachmentItemDataAccessor.getZoomNumberFromTag(attachmentTag);
            float[] zooms = TimelessAPI.getClientAttachmentIndex(scopeId).map(ClientAttachmentIndex::getZoom).orElse(null);
            if (zooms != null) {
                zoom = zooms[zoomNumber % zooms.length];
            }
        } else {
            zoom = TimelessAPI.getGunDisplay(gunItem).map(GunDisplayInstance::getIronZoom).orElse(1f);
        }
        return zoom;
    }

    @Override
    default boolean hasBulletInBarrel(ItemStack gun) {
        return NBTHelper.getTagValueFromItemStack(gun, GUN_HAS_BULLET_IN_BARREL, false);
    }

    @Override
    default void setBulletInBarrel(ItemStack gun, boolean bulletInBarrel) {
        NBTHelper.setCustomTagToItemStack(gun, t -> t.putBoolean(GUN_HAS_BULLET_IN_BARREL, bulletInBarrel));
    }
}
