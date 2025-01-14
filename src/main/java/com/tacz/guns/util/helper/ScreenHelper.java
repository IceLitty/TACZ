package com.tacz.guns.util.helper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

@Deprecated
@SuppressWarnings("removal")
@OnlyIn(Dist.CLIENT)
public class ScreenHelper {

    public static final ResourceLocation BACKGROUND_LOCATION = ResourceLocation.withDefaultNamespace("textures/block/dirt.png");

    public static void renderBackground(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (screen.getMinecraft().level != null) {
            guiGraphics.fillGradient(0, 0, screen.width, screen.height, -1072689136, -804253680);
            NeoForge.EVENT_BUS.post(new ScreenEvent.BackgroundRendered(screen, guiGraphics));
        } else {
            ScreenHelper.renderDirtBackground(screen, guiGraphics);
        }
    }

    public static void renderDirtBackground(Screen screen, GuiGraphics guiGraphics) {
        guiGraphics.setColor(0.25F, 0.25F, 0.25F, 1.0F);
        int i = 32;
        guiGraphics.blit(BACKGROUND_LOCATION, 0, 0, 0, 0.0F, 0.0F, screen.width, screen.height, 32, 32);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        NeoForge.EVENT_BUS.post(new ScreenEvent.BackgroundRendered(screen, guiGraphics));
    }

}
