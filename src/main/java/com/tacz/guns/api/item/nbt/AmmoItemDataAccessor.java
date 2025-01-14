package com.tacz.guns.api.item.nbt;

import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.util.helper.NBTHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public interface AmmoItemDataAccessor extends IAmmo {
    String AMMO_ID_TAG = "AmmoId";

    @Override
    @Nonnull
    default ResourceLocation getAmmoId(ItemStack ammo) {
        String _gunId = NBTHelper.getTagValueFromItemStack(ammo, AMMO_ID_TAG, (String) null);
        if (_gunId != null) {
            ResourceLocation gunId = ResourceLocation.tryParse(_gunId);
            return Objects.requireNonNullElse(gunId, DefaultAssets.EMPTY_AMMO_ID);
        }
        return DefaultAssets.EMPTY_AMMO_ID;
    }

    @Override
    default void setAmmoId(ItemStack ammo, @Nullable ResourceLocation ammoId) {
        NBTHelper.setCustomTagToItemStack(ammo, t -> t.putString(AMMO_ID_TAG, ammoId == null ? DefaultAssets.DEFAULT_AMMO_ID.toString() : ammoId.toString()));
    }

    @Override
    default boolean isAmmoOfGun(ItemStack gun, ItemStack ammo) {
        if (gun.getItem() instanceof IGun iGun && ammo.getItem() instanceof IAmmo iAmmo) {
            ResourceLocation gunId = iGun.getGunId(gun);
            ResourceLocation ammoId = iAmmo.getAmmoId(ammo);
            return TimelessAPI.getCommonGunIndex(gunId).map(gunIndex -> gunIndex.getGunData().getAmmoId().equals(ammoId)).orElse(false);
        }
        return false;
    }
}
