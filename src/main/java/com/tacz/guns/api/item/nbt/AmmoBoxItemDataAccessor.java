package com.tacz.guns.api.item.nbt;

import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.util.helper.NBTHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface AmmoBoxItemDataAccessor extends IAmmoBox {
    String AMMO_ID_TAG = "AmmoId";
    String AMMO_COUNT_TAG = "AmmoCount";
    String CREATIVE_TAG = "Creative";
    String ALL_TYPE_CREATIVE_TAG = "AllTypeCreative";
    String LEVEL_TAG = "Level";

    @Override
    default ResourceLocation getAmmoId(ItemStack ammoBox) {
        String resourceId = NBTHelper.getTagValueFromItemStack(ammoBox, AMMO_ID_TAG, (String) null);
        if (resourceId != null) {
            return ResourceLocation.parse(resourceId);
        }
        return DefaultAssets.EMPTY_AMMO_ID;
    }

    @Override
    default void setAmmoId(ItemStack ammoBox, ResourceLocation ammoId) {
        NBTHelper.setCustomTagToItemStack(ammoBox, tag -> tag.putString(AMMO_ID_TAG, ammoId.toString()));
    }

    @Override
    default int getAmmoCount(ItemStack ammoBox) {
        if (isAllTypeCreative(ammoBox) || isCreative(ammoBox)) {
            return Integer.MAX_VALUE;
        }
        return NBTHelper.getTagValueFromItemStack(ammoBox, AMMO_COUNT_TAG, 0);
    }

    @Override
    default void setAmmoCount(ItemStack ammoBox, int count) {
        if (isCreative(ammoBox)) {
            NBTHelper.setCustomTagToItemStack(ammoBox, tag -> tag.putInt(AMMO_COUNT_TAG, Integer.MAX_VALUE));
            return;
        }
        NBTHelper.setCustomTagToItemStack(ammoBox, tag -> tag.putInt(AMMO_COUNT_TAG, count));
    }

    @Override
    default boolean isAmmoBoxOfGun(ItemStack gun, ItemStack ammoBox) {
        if (gun.getItem() instanceof IGun iGun && ammoBox.getItem() instanceof IAmmoBox iAmmoBox) {
            if (isAllTypeCreative(ammoBox)) {
                return true;
            }
            ResourceLocation ammoId = iAmmoBox.getAmmoId(ammoBox);
            if (ammoId.equals(DefaultAssets.EMPTY_AMMO_ID)) {
                return false;
            }
            ResourceLocation gunId = iGun.getGunId(gun);
            return TimelessAPI.getCommonGunIndex(gunId).map(gunIndex -> gunIndex.getGunData().getAmmoId().equals(ammoId)).orElse(false);
        }
        return false;
    }

    @Override
    default ItemStack setAmmoLevel(ItemStack ammoBox, int level) {
        NBTHelper.setCustomTagToItemStack(ammoBox, tag -> tag.putInt(LEVEL_TAG, Math.max(level, 0)));
        return ammoBox;
    }

    @Override
    default int getAmmoLevel(ItemStack ammoBox) {
        return NBTHelper.getTagValueFromItemStack(ammoBox, LEVEL_TAG, 0);
    }

    @Override
    default boolean isCreative(ItemStack ammoBox) {
        return NBTHelper.getTagValueFromItemStack(ammoBox, CREATIVE_TAG, false);
    }

    @Override
    default boolean isAllTypeCreative(ItemStack ammoBox) {
        return NBTHelper.getTagValueFromItemStack(ammoBox, ALL_TYPE_CREATIVE_TAG, false);
    }

    @Override
    default ItemStack setCreative(ItemStack ammoBox, boolean isAllType) {
        if (isAllType) {
            NBTHelper.removeCustomTagToItemStack(ammoBox, CREATIVE_TAG);
            NBTHelper.setCustomTagToItemStack(ammoBox, t -> t.putBoolean(ALL_TYPE_CREATIVE_TAG, true));
            return ammoBox;
        }
        NBTHelper.removeCustomTagToItemStack(ammoBox, ALL_TYPE_CREATIVE_TAG);
        NBTHelper.setCustomTagToItemStack(ammoBox, t -> t.putBoolean(CREATIVE_TAG, true));
        return ammoBox;
    }
}
