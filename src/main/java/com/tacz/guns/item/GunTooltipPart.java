package com.tacz.guns.item;

import com.tacz.guns.util.helper.ItemStackHelper;
import com.tacz.guns.util.helper.NBTHelper;
import net.minecraft.world.item.ItemStack;

public enum GunTooltipPart {
    DESCRIPTION,
    AMMO_INFO,
    BASE_INFO,
    EXTRA_DAMAGE_INFO,
    UPGRADES_TIP,
    PACK_INFO;

    private final int mask = 1 << this.ordinal();

    public int getMask() {
        return this.mask;
    }

    public static int getHideFlags(ItemStack stack) {
        Number hideFlags = NBTHelper.getTagValueFromItemStack(stack, "HideFlags", (Number) null);
        if (hideFlags != null) {
            return hideFlags.intValue();
        }
        return ItemStackHelper.getDefaultTooltipHideFlags(stack);
    }

    public static void setHideFlags(ItemStack stack, int mask) {
        NBTHelper.setCustomTagToItemStack(stack, t -> t.putInt("HideFlags", mask));
    }
}
