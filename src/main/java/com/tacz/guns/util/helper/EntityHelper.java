package com.tacz.guns.util.helper;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

@SuppressWarnings("JavadocReference")
public class EntityHelper {

    /**
     * {@link net.minecraft.world.entity.Entity#setSecondsOnFire}
     */
    public static void setSecondsOnFire(Entity entity, int seconds) {
        int i = seconds * 20;
        if (entity instanceof LivingEntity) {
            i = ProtectionEnchantmentHelper.getFireAfterDampener((LivingEntity) entity, i);
        }
        if (entity.getRemainingFireTicks() < i) {
            entity.setRemainingFireTicks(i);
        }

    }

    /**
     * {@link net.minecraft.world.entity.Entity#doEnchantDamageEffects}
     */
    public static void doEnchantDamageEffects(LivingEntity source, Entity target) {
        if (target instanceof LivingEntity) {
            EnchantmentHelperHelper.doPostHurtEffects((LivingEntity) target, source);
        }
        EnchantmentHelperHelper.doPostDamageEffects(source, target);
    }

}
