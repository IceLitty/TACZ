package com.tacz.guns.util.helper;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.*;

import javax.annotation.Nullable;
import java.util.Map;

@SuppressWarnings("JavadocReference")
public class EnchantmentHelperHelper {

    /**
     * Unlike {@link EnchantmentHelper#getEnchantmentLevel(Holder, LivingEntity)},
     * this method use NBT function to get the largest target enchantment level from entity equipments
     */
    public static int getEnchantmentLevel(ResourceKey<Enchantment> enchantment, LivingEntity entity) {
        int max = 0;
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            ItemStack slot = entity.getItemBySlot(equipmentSlot);
            ItemEnchantments enchantments = slot.getTagEnchantments();
            for (Object2IntMap.Entry<Holder<Enchantment>> holderEntry : enchantments.entrySet()) {
                Holder<Enchantment> key = holderEntry.getKey();
                if (key.is(enchantment)) {
                    max = Math.max(max, holderEntry.getIntValue());
                }
            }
        }
        return max;
    }

    /**
     * {@link net.minecraft.world.item.enchantment.EnchantmentHelper#doPostHurtEffects}
     */
    public static void doPostHurtEffects(LivingEntity target, Entity source) {
        if (target != null && target.level() instanceof ServerLevel serverLevel) {
            EnchantedItemInUse enchantedItemInUse = new EnchantedItemInUse(ItemStack.EMPTY, EquipmentSlot.MAINHAND, target);
            Holder.Reference<DamageType> damageType = serverLevel.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC);
            DamageSource damageSource = new DamageSource(damageType, target, source);
            EnchantmentHelper.EnchantmentVisitor enchantmenthelper$enchantmentvisitor = (enchantment, lvl) -> {
                enchantment.value().doPostAttack(serverLevel, lvl, enchantedItemInUse, EnchantmentTarget.DAMAGING_ENTITY, target, damageSource);
            };
            runIterationOnInventory(enchantmenthelper$enchantmentvisitor, target.getAllSlots());
        }
//        if(false) // Forge: Fix MC-248272
//            if (source instanceof Player) {
//                runIterationOnItem(enchantmenthelper$enchantmentvisitor, target.getMainHandItem());
//            }
    }

    /**
     * {@link net.minecraft.world.item.enchantment.EnchantmentHelper#doPostDamageEffects}
     */
    public static void doPostDamageEffects(LivingEntity source, Entity target) {
        if (source != null && source.level() instanceof ServerLevel serverLevel) {
            EnchantedItemInUse enchantedItemInUse = new EnchantedItemInUse(ItemStack.EMPTY, EquipmentSlot.MAINHAND, source);
            Holder.Reference<DamageType> damageType = serverLevel.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC);
            DamageSource damageSource = new DamageSource(damageType, target, source);
            EnchantmentHelper.EnchantmentVisitor enchantmenthelper$enchantmentvisitor = (enchantment, lvl) -> {
                enchantment.value().doPostAttack(serverLevel, lvl, enchantedItemInUse, EnchantmentTarget.DAMAGING_ENTITY, target, damageSource);
            };
            runIterationOnInventory(enchantmenthelper$enchantmentvisitor, source.getAllSlots());
        }
//        if(false) // Forge: Fix MC-248272
//            if (source instanceof Player) {
//                runIterationOnItem(enchantmenthelper$enchantmentvisitor, source.getMainHandItem());
//            }
    }

    /**
     * {@link net.minecraft.world.item.enchantment.EnchantmentHelper#runIterationOnInventory}
     */
    private static void runIterationOnInventory(EnchantmentHelper.EnchantmentVisitor enchantmentVisitor, Iterable<ItemStack> inventory) {
        for (ItemStack itemstack : inventory) {
            runIterationOnItem(enchantmentVisitor, itemstack);
        }
    }

    /**
     * {@link EnchantmentHelper#runIterationOnItem(ItemStack, EnchantmentHelper.EnchantmentVisitor)}
     */
    private static void runIterationOnItem(EnchantmentHelper.EnchantmentVisitor enchantmentVisitor, ItemStack itemStack) {
        if (!itemStack.isEmpty()) {
            if (true) { // forge: redirect enchantment logic to allow non-NBT enchants
                ItemEnchantments enchantments = itemStack.get(DataComponents.ENCHANTMENTS);
                if (enchantments != null) {
                    for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
                        enchantmentVisitor.accept(entry.getKey(), entry.getIntValue());
                    }
                }
                return;
            }
            ItemEnchantments tags = itemStack.getTagEnchantments();
            for (Object2IntMap.Entry<Holder<Enchantment>> entry : tags.entrySet()) {
                Holder<Enchantment> key = entry.getKey();
                int lvl = entry.getIntValue();
                enchantmentVisitor.accept(key, lvl);
            }
        }
    }

}
