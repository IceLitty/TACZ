package com.tacz.guns.event;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.KnockBackModifier;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = GunMod.MOD_ID)
public class KnockbackChange {
    @SubscribeEvent
    public static void onKnockback(LivingKnockBackEvent event) {
        KnockBackModifier modifier = KnockBackModifier.fromLivingEntity(event.getEntity());
        double strength = modifier.getKnockBackStrength();
        if (strength >= 0) {
            event.setStrength((float) strength);
        }
    }
}
