package com.tacz.guns.client.extensions;

import com.tacz.guns.client.renderer.item.GunSmithTableItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

@OnlyIn(Dist.CLIENT)
public class GunSmithTableItemExtensions implements IClientItemExtensions {

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        Minecraft minecraft = Minecraft.getInstance();
        return new GunSmithTableItemRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
    }

}
