package com.tacz.guns.util.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("JavadocReference")
public class ClientPlayerHelper {

    /**
     * {@link net.minecraft.client.player.AbstractClientPlayer#getSkinTextureLocation}
     */
    public static ResourceLocation getSkinTextureLocation(AbstractClientPlayer player) {
        PlayerInfo playerInfo = null;
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            playerInfo = connection.getPlayerInfo(player.getUUID());
        }
        return playerInfo == null ? DefaultPlayerSkin.get(player.getUUID()).texture() : DefaultPlayerSkin.get(player.getGameProfile().getId()).texture();
    }

}
