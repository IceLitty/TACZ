package com.tacz.guns.api.item.nbt;

import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.item.IBlock;
import com.tacz.guns.util.helper.NBTHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public interface BlockItemDataAccessor extends IBlock {
    String BLOCK_ID = "BlockId";

    @Override
    @Nonnull
    default ResourceLocation getBlockId(ItemStack block) {
        String resourceId = NBTHelper.getTagValueFromItemStack(block, BLOCK_ID, (String) null);
        if (resourceId != null) {
            ResourceLocation gunId = ResourceLocation.tryParse(resourceId);
            return Objects.requireNonNullElse(gunId, DefaultAssets.EMPTY_BLOCK_ID);
        }
        return DefaultAssets.EMPTY_BLOCK_ID;
    }

    @Override
    default void setBlockId(ItemStack block, @Nullable ResourceLocation blockId) {
        if (blockId != null) {
            NBTHelper.setCustomTagToItemStack(block, t -> t.putString(BLOCK_ID, blockId.toString()));
            return;
        }
        NBTHelper.setCustomTagToItemStack(block, t -> t.putString(BLOCK_ID, DefaultAssets.EMPTY_BLOCK_ID.toString()));
    }

}
