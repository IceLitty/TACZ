package com.tacz.guns.init;

import com.mojang.serialization.MapCodec;
import com.tacz.guns.GunMod;
import com.tacz.guns.block.*;
import com.tacz.guns.block.entity.GunSmithTableBlockEntity;
import com.tacz.guns.block.entity.StatueBlockEntity;
import com.tacz.guns.block.entity.TargetBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, GunMod.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends Block>> BLOCK_REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK_TYPE, GunMod.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, GunMod.MOD_ID);

    // 旧方块就让他独占一个了
    public static DeferredHolder<Block, GunSmithTableBlockB> GUN_SMITH_TABLE = BLOCKS.register("gun_smith_table", () -> new GunSmithTableBlockB(AbstractGunSmithTableBlock.getDefaultProperties()));
    public static DeferredHolder<Block, GunSmithTableBlockA> WORKBENCH_111 = BLOCKS.register("workbench_a", () -> new GunSmithTableBlockA(AbstractGunSmithTableBlock.getDefaultProperties()));
    public static DeferredHolder<Block, GunSmithTableBlockB> WORKBENCH_211 = BLOCKS.register("workbench_b", () -> new GunSmithTableBlockB(AbstractGunSmithTableBlock.getDefaultProperties()));
    public static DeferredHolder<Block, GunSmithTableBlockC> WORKBENCH_121 = BLOCKS.register("workbench_c", () -> new GunSmithTableBlockC(AbstractGunSmithTableBlock.getDefaultProperties()));
    public static DeferredHolder<MapCodec<? extends Block>, MapCodec<GunSmithTableBlockA>> WORKBENCH_111_CODEC = BLOCK_REGISTER.register("workbench_a", () -> GunSmithTableBlockA.CODEC);
    public static DeferredHolder<MapCodec<? extends Block>, MapCodec<GunSmithTableBlockB>> WORKBENCH_222_CODEC = BLOCK_REGISTER.register("workbench_b", () -> GunSmithTableBlockB.CODEC);
    public static DeferredHolder<MapCodec<? extends Block>, MapCodec<GunSmithTableBlockC>> WORKBENCH_333_CODEC = BLOCK_REGISTER.register("workbench_c", () -> GunSmithTableBlockC.CODEC);

    public static DeferredHolder<Block, TargetBlock> TARGET = BLOCKS.register("target", () -> new TargetBlock(TargetBlock.getDefaultProperties()));
    public static DeferredHolder<Block, StatueBlock> STATUE = BLOCKS.register("statue", () -> new StatueBlock(StatueBlock.getDefaultProperties()));
    public static DeferredHolder<MapCodec<? extends Block>, MapCodec<TargetBlock>> TARGET_CODEC = BLOCK_REGISTER.register("target", () -> TargetBlock.CODEC);
    public static DeferredHolder<MapCodec<? extends Block>, MapCodec<StatueBlock>> STATUE_CODEC = BLOCK_REGISTER.register("statue", () -> StatueBlock.CODEC);

    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<GunSmithTableBlockEntity>> GUN_SMITH_TABLE_BE = TILE_ENTITIES.register("gun_smith_table", () -> GunSmithTableBlockEntity.TYPE);
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<TargetBlockEntity>> TARGET_BE = TILE_ENTITIES.register("target", () -> TargetBlockEntity.TYPE);
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<StatueBlockEntity>> STATUE_BE = TILE_ENTITIES.register("statue", () -> StatueBlockEntity.TYPE);
    public static final TagKey<Block> BULLET_IGNORE_BLOCKS = BlockTags.create(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "bullet_ignore"));
}
