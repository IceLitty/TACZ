package com.tacz.guns.util.helper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapLike;
import com.tacz.guns.GunMod;

public class MapCodecHelper {

    public static <T> JsonObject turnMapLikeBackToJsonObject(MapLike<T> input) {
        JsonObject jsonObject = new JsonObject();
        input.entries().forEach(entry -> {
            if (entry.getFirst() instanceof JsonElement && entry.getSecond() instanceof JsonObject obj) {
                jsonObject.add(String.valueOf(entry.getFirst()), obj);
            } else {
                GunMod.LOGGER.warn("Invalid map entry when turn MapLike to JsonObject: " + entry);
            }
        });
        return jsonObject;
    }

}
