package com.tacz.guns.util.helper;

import com.google.gson.*;
import com.mojang.serialization.MapLike;
import com.tacz.guns.GunMod;

public class MapCodecHelper {

    public static <T> JsonObject turnMapLikeBackToJsonObject(MapLike<T> input) {
        JsonObject jsonObject = new JsonObject();
        input.entries().forEach(entry -> {
            if (entry.getFirst() instanceof JsonPrimitive key && entry.getSecond() instanceof JsonElement value) {
                jsonObject.add(key.getAsString(), value);
            } else {
                GunMod.LOGGER.warn("Invalid map entry when turn MapLike to JsonObject: {}", entry);
            }
        });
        return jsonObject;
    }

}
