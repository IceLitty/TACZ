package com.tacz.guns.util.helper;

import net.minecraft.client.Minecraft;

public class MinecraftHelper {

    public static float getFrameTime() {
        return Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
    }

}
