package com.tacz.guns.util.helper;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

@SuppressWarnings("JavadocReference")
public class ProtectionEnchantmentHelper {

    /**
     * {@link net.minecraft.world.item.enchantment.ProtectionEnchantment#getFireAfterDampener}
     */
    public static int getFireAfterDampener(LivingEntity entity, int ticks) {
        int i = EnchantmentHelperHelper.getEnchantmentLevel(Enchantments.FIRE_PROTECTION, entity);
        if (i > 0) {
            ticks -= Mth.floor((float)ticks * (float)i * 0.15F);
        }
        return ticks;
    }

    /**
     * {@link net.minecraft.world.item.enchantment.ProtectionEnchantment#getExplosionKnockbackAfterDampener}
     */
    public static double getExplosionKnockbackAfterDampener(LivingEntity entity, double damage) {
        int i = EnchantmentHelperHelper.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, entity);
        if (i > 0) {
            damage *= Mth.clamp(1.0D - (double)i * 0.15D, 0.0D, 1.0D);
        }
        return damage;
    }

}
