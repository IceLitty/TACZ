package com.tacz.guns.util.helper;

import com.google.gson.*;
import com.tacz.guns.GunMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings("JavadocReference")
public class IngredientHelper {

    /**
     * {@link Ingredient#fromValues(Stream)}
     */
    public static Ingredient fromValues(Stream<? extends Ingredient.Value> p_43939_) {
        try {
            Constructor<Ingredient> constructor = Ingredient.class.getDeclaredConstructor(Stream.class);
            constructor.setAccessible(true);
            Ingredient ingredient = constructor.newInstance(p_43939_);
            return ingredient.isEmpty() ? Ingredient.EMPTY : ingredient;
        } catch (Exception e) {
            GunMod.LOGGER.error("Ingredient create from stream has error:", e);
            return Ingredient.EMPTY;
        }
    }

    /**
     * {@link net.minecraft.world.item.crafting.Ingredient#fromJson(com.google.gson.JsonElement)}
     */
    public static Ingredient fromJson(@Nullable JsonElement p_43918_) {
        return fromJson(p_43918_, true);
    }

    /**
     * {@link net.minecraft.world.item.crafting.Ingredient#fromJson(com.google.gson.JsonElement, boolean)}
     */
    public static Ingredient fromJson(@Nullable JsonElement p_289022_, boolean p_288974_) {
        if (p_289022_ != null && !p_289022_.isJsonNull()) {
            JsonObject ingJson = p_289022_.getAsJsonObject();
            // TODO fix when enable this with recipe json {"materials":[{"item":{"type":"forge:partial_nbt","item":"tacz:ammo","nbt":{"tacz:data":{"AmmoId":"converted:enduring_victory_pt"}}},"count":6},{"item":{"type":"forge:partial_nbt","item":"tacz:attachment","nbt":{"tacz:data":{"AttachmentId":"tacz:oem_stock_heavy"}},"count":1}}],"result":{"type":"attachment","id":"converted:416sap_heavystock"},"type":"tacz:gun_smith_table_crafting"}
//            boolean readNbt = ingJson.has("nbt");
//            ItemStack itemStack = CraftingHelper.getItemStack(ingJson, readNbt);
//            if (itemStack != null) {
//                Ingredient ingredient;
//                if (readNbt) {
//                    ingredient = DataComponentIngredient.of(false, itemStack);
//                } else {
//                    ingredient = Ingredient.of(itemStack);
//                }
//                if (ingredient != null)
//                    return ingredient;
//            }
            if (p_289022_.isJsonObject()) {
                return fromValues(Stream.of(valueFromJson(ingJson)));
            } else if (p_289022_.isJsonArray()) {
                JsonArray jsonarray = p_289022_.getAsJsonArray();
                if (jsonarray.size() == 0 && !p_288974_) {
                    throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
                } else {
                    return fromValues(StreamSupport.stream(jsonarray.spliterator(), false).map((p_289756_) -> {
                        return valueFromJson(GsonHelper.convertToJsonObject(p_289756_, "item"));
                    }));
                }
            } else {
                throw new JsonSyntaxException("Expected item to be object or array of objects");
            }
        } else {
            throw new JsonSyntaxException("Item cannot be null");
        }
    }

    /**
     * {@link net.minecraft.world.item.crafting.Ingredient#valueFromJson}
     */
    public static Ingredient.Value valueFromJson(JsonObject p_289797_) {
        if (p_289797_.has("item") && p_289797_.has("tag")) {
            throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
        } else if (p_289797_.has("item")) {
            Item item = ShapedRecipeHelper.itemFromJson(p_289797_);
            return new Ingredient.ItemValue(new ItemStack(item));
        } else if (p_289797_.has("tag")) {
            ResourceLocation resourcelocation = ResourceLocation.tryParse(GsonHelper.getAsString(p_289797_, "tag"));
            TagKey<Item> tagkey = TagKey.create(Registries.ITEM, resourcelocation);
            return new Ingredient.TagValue(tagkey);
        } else {
            throw new JsonParseException("An ingredient entry needs either a tag or an item");
        }
    }

//    /**
//     * {@link net.minecraft.world.item.crafting.Ingredient#merge}
//     */
//    public static Ingredient merge(Collection<Ingredient> parts) {
//        return fromValues(parts.stream().flatMap(i -> Arrays.stream(i.getValues())));
//    }

}
