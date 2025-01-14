package com.tacz.guns.init;

import com.tacz.guns.GunMod;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModComponents {

    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, GunMod.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CustomData>> CUSTOM_DATA =
            REGISTRAR.registerComponentType("data", builder -> builder.persistent(CustomData.CODEC));

}
