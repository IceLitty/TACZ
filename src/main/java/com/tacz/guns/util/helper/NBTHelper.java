package com.tacz.guns.util.helper;

import com.mojang.authlib.GameProfile;
import com.tacz.guns.init.ModComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.*;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.UUID;
import java.util.function.Consumer;

public class NBTHelper {

    /**
     * Original code like
     * <pre>
     * {@code
     *  CompoundTag itemTag = result.getResult().getOrCreateTag();
     *  for (String key : extraTag.getAllKeys()) {
     *      Tag tag = extraTag.get(key);
     *      if (tag != null) {
     *          itemTag.put(key, tag);
     *      }
     *  }}
     * </pre>
     */
    public static void setCustomTagToItemStack(ItemStack itemStack, CompoundTag extraTag) {
        CustomData customData = itemStack.get(ModComponents.CUSTOM_DATA);
        if (customData == null) {
            customData = CustomData.EMPTY;
            itemStack.set(ModComponents.CUSTOM_DATA, customData);
        }
        for (String key : extraTag.getAllKeys()) {
            Tag tag = extraTag.get(key);
            if (tag != null) {
                CustomData updated = customData.update(t -> t.put(key, tag));
                itemStack.set(ModComponents.CUSTOM_DATA, updated);
            }
        }
    }

    /**
     * Original code like
     * <pre>
     * {@code
     *  CompoundTag tag = ammoBox.getOrCreateTag();
     *  tag.putString(AMMO_ID_TAG, ammoId.toString());
     * }
     * </pre>
     */
    public static void setCustomTagToItemStack(ItemStack itemStack, Consumer<CompoundTag> modifier) {
        CompoundTag tag = new CompoundTag();
        modifier.accept(tag);
        setCustomTagToItemStack(itemStack, tag);
    }

    /**
     * Original code like
     * <pre>
     * {@code
     *  if (tag.contains(CREATIVE_TAG, Tag.TAG_BYTE)) {
     *      tag.remove(CREATIVE_TAG);
     *  }
     * }
     * </pre>
     */
    public static void removeCustomTagToItemStack(ItemStack itemStack, String tagName) {
        CustomData customData = itemStack.get(ModComponents.CUSTOM_DATA);
        if (customData != null && customData.contains(tagName)) {
            CustomData updated = customData.update(t -> t.remove(tagName));
            itemStack.set(ModComponents.CUSTOM_DATA, updated);
        }
    }

    /**
     * Original code like (ONLY FOR READ ONLY) (RETURN NULLABLE)
     * <pre>
     * {@code
     *  CompoundTag nbt = attachmentStack.getOrCreateTag();
     *  // and used for another way
     * }
     * </pre>
     */
    public static CompoundTag getCustomTagFromItemStackReadonly(ItemStack itemStack) {
        CustomData customData = itemStack.get(ModComponents.CUSTOM_DATA);
        if (customData == null) {
            return null;
        }
        return customData.copyTag();
    }

    /**
     * Original code like
     * <pre>
     * {@code
     *  CompoundTag tag = ammoBox.getOrCreateTag();
     *  if (tag.contains(AMMO_COUNT_TAG, Tag.TAG_INT)) {
     *      return tag.getInt(AMMO_COUNT_TAG);
     *  }
     * }
     * </pre>
     */
    public static Integer getTagValueFromItemStack(ItemStack itemStack, String tagKey, Integer defaultValue) {
        CustomData customData = itemStack.get(ModComponents.CUSTOM_DATA);
        if (customData != null && customData.contains(tagKey)) {
            Tag tag = customData.copyTag().get(tagKey);
            if (tag instanceof IntTag _tag) {
                return _tag.getAsInt();
            }
        }
        return defaultValue;
    }

    public static String getTagValueFromItemStack(ItemStack itemStack, String tagKey, String defaultValue) {
        CustomData customData = itemStack.get(ModComponents.CUSTOM_DATA);
        if (customData != null && customData.contains(tagKey)) {
            Tag tag = customData.copyTag().get(tagKey);
            if (tag instanceof StringTag _tag) {
                return _tag.getAsString();
            }
        }
        return defaultValue;
    }

    public static Boolean getTagValueFromItemStack(ItemStack itemStack, String tagKey, Boolean defaultValue) {
        CustomData customData = itemStack.get(ModComponents.CUSTOM_DATA);
        if (customData != null && customData.contains(tagKey)) {
            Tag tag = customData.copyTag().get(tagKey);
            if (tag instanceof ByteTag _tag) {
                return _tag.getAsByte() == (byte) 1;
            }
        }
        return defaultValue;
    }

    public static Byte getTagValueFromItemStack(ItemStack itemStack, String tagKey, Byte defaultValue) {
        CustomData customData = itemStack.get(ModComponents.CUSTOM_DATA);
        if (customData != null && customData.contains(tagKey)) {
            Tag tag = customData.copyTag().get(tagKey);
            if (tag instanceof ByteTag _tag) {
                return _tag.getAsByte();
            }
        }
        return defaultValue;
    }

    /**
     * {@link net.minecraft.nbt.NbtUtils#readGameProfile}
     */
    @SuppressWarnings("JavadocReference")
    public static GameProfile readGameProfile(CompoundTag ownerTag) {
        String s = null;
        UUID uuid = null;
        if (ownerTag.contains("Name", 8)) {
            s = ownerTag.getString("Name");
        }
        if (ownerTag.hasUUID("Id")) {
            uuid = ownerTag.getUUID("Id");
        }
        try {
            GameProfile gameprofile = new GameProfile(uuid, s);
            if (ownerTag.contains("Properties", 10)) {
                CompoundTag compoundtag = ownerTag.getCompound("Properties");
                for(String s1 : compoundtag.getAllKeys()) {
                    ListTag listtag = compoundtag.getList(s1, 10);
                    for(int i = 0; i < listtag.size(); ++i) {
                        CompoundTag compoundtag1 = listtag.getCompound(i);
                        String s2 = compoundtag1.getString("Value");
                        if (compoundtag1.contains("Signature", 8)) {
                            gameprofile.getProperties().put(s1, new com.mojang.authlib.properties.Property(s1, s2, compoundtag1.getString("Signature")));
                        } else {
                            gameprofile.getProperties().put(s1, new com.mojang.authlib.properties.Property(s1, s2));
                        }
                    }
                }
            }
            return gameprofile;
        } catch (Throwable throwable) {
            return null;
        }
    }

    /**
     * {@link net.minecraft.nbt.NbtUtils#writeGameProfile}
     */
    @SuppressWarnings("JavadocReference")
    public static CompoundTag writeGameProfile(CompoundTag tag, GameProfile gameProfile) {
        if (!StringUtil.isNullOrEmpty(gameProfile.getName())) {
            tag.putString("Name", gameProfile.getName());
        }
        if (gameProfile.getId() != null) {
            tag.putUUID("Id", gameProfile.getId());
        }
        if (!gameProfile.getProperties().isEmpty()) {
            CompoundTag compoundtag = new CompoundTag();
            for(String s : gameProfile.getProperties().keySet()) {
                ListTag listtag = new ListTag();
                for(com.mojang.authlib.properties.Property property : gameProfile.getProperties().get(s)) {
                    CompoundTag compoundtag1 = new CompoundTag();
                    compoundtag1.putString("Value", property.value());
                    if (property.hasSignature()) {
                        compoundtag1.putString("Signature", property.signature());
                    }
                    listtag.add(compoundtag1);
                }
                compoundtag.put(s, listtag);
            }
            tag.put("Properties", compoundtag);
        }
        return tag;
    }

}
