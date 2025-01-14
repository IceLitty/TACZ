package com.tacz.guns.util.helper;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

@SuppressWarnings("JavadocReference")
public class ShapedRecipeHelper {

    /**
     * {@link net.minecraft.world.item.crafting.ShapedRecipe#itemFromJson}
     */
    public static Item itemFromJson(JsonObject p_151279_) {
        String s = GsonHelper.getAsString(p_151279_, "item");
        Item item = BuiltInRegistries.ITEM.getOptional(ResourceLocation.tryParse(s)).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown item '" + s + "'");
        });
        if (item == Items.AIR) {
            throw new JsonSyntaxException("Empty ingredient not allowed here");
        } else {
            return item;
        }
    }

}
