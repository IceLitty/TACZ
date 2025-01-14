package com.tacz.guns.event;

import com.tacz.guns.GunMod;
import com.tacz.guns.client.extensions.AbstractGunItemExtensions;
import com.tacz.guns.client.extensions.AmmoItemExtensions;
import com.tacz.guns.client.extensions.AttachmentItemExtensions;
import com.tacz.guns.client.extensions.GunSmithTableItemExtensions;
import com.tacz.guns.init.ModItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = GunMod.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientExtensionsRegisterEvent {

    /**
     * 注册客户端扩展事件
     */
    @SubscribeEvent
    public static void register(RegisterClientExtensionsEvent event) {
        event.registerItem(new AmmoItemExtensions(), ModItems.AMMO.get());
        event.registerItem(new GunSmithTableItemExtensions(),
                ModItems.GUN_SMITH_TABLE.get(), ModItems.WORKBENCH_111.get(), ModItems.WORKBENCH_211.get(), ModItems.WORKBENCH_121.get());
        event.registerItem(new AttachmentItemExtensions(), ModItems.ATTACHMENT.get());
        event.registerItem(new AbstractGunItemExtensions(), ModItems.MODERN_KINETIC_GUN.get());
    }

}
