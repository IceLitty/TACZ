package com.tacz.guns.crafting;

import com.google.gson.JsonObject;
import com.mojang.serialization.*;
import com.tacz.guns.GunMod;
import com.tacz.guns.crafting.result.GunSmithTableResult;
import com.tacz.guns.resource.CommonAssetsManager;
import com.tacz.guns.resource.pojo.data.recipe.TableRecipe;
import com.tacz.guns.util.helper.MapCodecHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 此类为数据包侧载枪械工作台的实现<br>
 * 枪包的序列化不在此处
 */
public class GunSmithTableSerializer implements RecipeSerializer<GunSmithTableRecipe> {

    public static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (int i=0; i < b.length; i++) {
            result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }

    @Override
    public MapCodec<GunSmithTableRecipe> codec() {
        return new MapCodec<>() {
            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.empty();
            }
            @Override
            public <T> DataResult<GunSmithTableRecipe> decode(DynamicOps<T> ops, MapLike<T> input) {
                JsonObject jsonObject = MapCodecHelper.turnMapLikeBackToJsonObject(input);
                TableRecipe tableRecipe = CommonAssetsManager.GSON.fromJson(jsonObject, TableRecipe.class);
                if (tableRecipe != null) {
                    // fixed id at com.tacz.guns.event.FixRecipeIdByServerStartedEvent
                    String id = byteArrayToHexString(Base64.getEncoder().encode(jsonObject.toString().getBytes(StandardCharsets.UTF_8)));
                    return new DataResult.Success<>(new GunSmithTableRecipe(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, id), tableRecipe), Lifecycle.stable());
                }
                return new DataResult.Error<>(() -> "Input is not valid gun smith table recipe.", Optional.empty(), Lifecycle.stable());
            }
            @Override
            public <T> RecordBuilder<T> encode(GunSmithTableRecipe input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                GunMod.LOGGER.error("Not implemented yet when encode recipe " + input);
                return prefix;
            }
        };
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, GunSmithTableRecipe> streamCodec() {
        return new StreamCodec<>() {
            @Override
            public void encode(RegistryFriendlyByteBuf buffer, GunSmithTableRecipe recipe) {
                buffer.writeResourceLocation(recipe.getId());
                buffer.writeInt(recipe.getInputs().size());
                for (GunSmithTableIngredient ingredient : recipe.getInputs()) {
                    buffer.writeJsonWithCodec(Ingredient.CODEC, ingredient.getIngredient());
                    buffer.writeInt(ingredient.getCount());
                }
                buffer.writeJsonWithCodec(ItemStack.CODEC, recipe.getResult().getResult());
                buffer.writeUtf(recipe.getResult().getGroup());
            }
            @Override
            public GunSmithTableRecipe decode(RegistryFriendlyByteBuf buffer) {
                ResourceLocation recipeId = buffer.readResourceLocation();
                int size = buffer.readInt();
                List<GunSmithTableIngredient> ingredients = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    Ingredient ingredient = buffer.readJsonWithCodec(Ingredient.CODEC);
                    ingredients.add(new GunSmithTableIngredient(ingredient, buffer.readInt()));
                }
                ItemStack resultItem = buffer.readJsonWithCodec(ItemStack.CODEC);
                String group = buffer.readUtf();
                GunSmithTableResult result = new GunSmithTableResult(resultItem, group);
                return new GunSmithTableRecipe(recipeId, result, ingredients);
            }
        };
    }
}
