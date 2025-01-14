package com.tacz.guns.event;

import com.tacz.guns.GunMod;
import com.tacz.guns.init.ModItems;
import com.tacz.guns.item.AmmoBoxItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = GunMod.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ItemColorRegisterEvent {

    /**
     * 注册颜色处理器事件
     */
    @SubscribeEvent
    public static void register(final RegisterColorHandlersEvent.Item event) {
        event.register(AmmoBoxItem::getColor, ModItems.AMMO_BOX.get());
    }

}
