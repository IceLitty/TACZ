package com.tacz.guns.util.helper;

import com.tacz.guns.init.ModComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class ItemStackHelper {

    /**
     * same as net.minecraftforge.common.extensions.IForgeItem.getDefaultTooltipHideFlags
     */
    public static int getDefaultTooltipHideFlags(ItemStack stack) {
        return 0;
    }

    /**
     * {@link net.minecraft.world.item.ItemStack#ItemStack(net.minecraft.nbt.CompoundTag)}
     */
    @SuppressWarnings("JavadocReference")
    public static ItemStack of(CompoundTag baseTag) {
        Item rawItem = BuiltInRegistries.ITEM.get(ResourceLocation.parse(baseTag.getString("id")));
        int count = baseTag.getByte("Count");
        ItemStack itemStack = new ItemStack(rawItem, count);
        if (baseTag.contains("tag", Tag.TAG_COMPOUND)) {
            CompoundTag tag = baseTag.getCompound("tag");
            itemStack.set(ModComponents.CUSTOM_DATA, CustomData.of(tag));
            itemStack.getItem().verifyComponentsAfterLoad(itemStack);
        }
        if (itemStack.getItem().isDamageable(itemStack)) {
            itemStack.setDamageValue(itemStack.getDamageValue());
        }
        return itemStack;
    }

    /**
     * {@link net.minecraft.world.item.ItemStack#save(HolderLookup.Provider, Tag)}
     * 这个方法是将物品的标签储存至tag中
     */
    public static CompoundTag save(ItemStack itemStack, CompoundTag tag) {
        ResourceLocation resourcelocation = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
        tag.putString("id", resourcelocation.toString());
        tag.putByte("Count", (byte) itemStack.getCount());
        if (itemStack.has(ModComponents.CUSTOM_DATA)) {
            CustomData customData = itemStack.get(ModComponents.CUSTOM_DATA);
            if (customData != null) {
                tag.put("tag", customData.copyTag());
            }
        }
        return tag;
    }

}
