package com.tacz.guns.util.helper;

import com.mojang.authlib.GameProfile;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@SuppressWarnings("JavadocReference")
public class SkullBlockEntityHelper {

    /**
     * {@link net.minecraft.world.level.block.entity.SkullBlockEntity#updateGameprofile}
     */
    public static void updateGameprofile(@Nullable GameProfile gameProfile, Consumer<GameProfile> consumer) {
        if (gameProfile == null) {
            return;
        }
        CompletableFuture<Optional<GameProfile>> future = SkullBlockEntity.fetchGameProfile(gameProfile.getId());
        future.thenAccept(_gameProfile -> {
            _gameProfile.ifPresent(consumer);
        });
    }

}
