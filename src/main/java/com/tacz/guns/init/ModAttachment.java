package com.tacz.guns.init;

import com.tacz.guns.GunMod;
import com.tacz.guns.entity.sync.core.DataHolder;
import com.tacz.guns.entity.sync.core.DataHolderSerializer;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachment {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, GunMod.MOD_ID);
    public static final Supplier<AttachmentType<DataHolder>> DATA_HOLDER = ATTACHMENT_TYPES.register("data_holder", () -> AttachmentType.builder(DataHolder::new).serialize(new DataHolderSerializer()).build());

}
