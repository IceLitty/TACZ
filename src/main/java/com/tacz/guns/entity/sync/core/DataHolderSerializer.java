package com.tacz.guns.entity.sync.core;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ListTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

/**
 * Author: MrCrayfish
 * Open source at <a href="https://github.com/MrCrayfish/Framework">Github</a> under LGPL License.
 */
public class DataHolderSerializer implements IAttachmentSerializer<ListTag, DataHolder> {
    @Override
    public DataHolder read(IAttachmentHolder holder, ListTag list, HolderLookup.Provider provider) {
        DataHolder data = new DataHolder();
        data.deserialize(list, provider);
        return data;
    }

    @Override
    public ListTag write(DataHolder holder, HolderLookup.Provider provider) {
        return holder.serialize(provider);
    }
}
