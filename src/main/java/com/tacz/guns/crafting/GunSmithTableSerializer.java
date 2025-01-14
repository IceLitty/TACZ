package com.tacz.guns.crafting;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tacz.guns.crafting.result.GunSmithTableResult;
import com.tacz.guns.resource.CommonAssetsManager;
import com.tacz.guns.resource.pojo.data.recipe.TableRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 此类为数据包侧载枪械工作台的实现<br>
 * 枪包的序列化不在此处
 */
public class GunSmithTableSerializer implements RecipeSerializer<GunSmithTableRecipe> {
    @Nullable
    public GunSmithTableRecipe fromJson(ResourceLocation id, JsonObject jsonObject) {
        TableRecipe tableRecipe = CommonAssetsManager.GSON.fromJson(jsonObject, TableRecipe.class);
        if (tableRecipe != null) {
            return new GunSmithTableRecipe(id, tableRecipe);
        }
        return null;
    }

    @Override
    public MapCodec<GunSmithTableRecipe> codec() {
        return RecordCodecBuilder.mapCodec(inst -> inst.group(
                ResourceLocation.CODEC.fieldOf("recipeId").forGetter(GunSmithTableRecipe::getId),
                Codec.pair(Ingredient.CODEC, Codec.INT).listOf().fieldOf("ingredient").forGetter(r -> r.getInputs().stream().map(_r -> new Pair<>(_r.getIngredient(),_r.getCount())).toList()),
                ItemStack.CODEC.fieldOf("result").forGetter(r -> r.getResult().getResult()),
                Codec.STRING.fieldOf("group").forGetter(r -> r.getResult().getGroup())
        ).apply(inst, (recipeId, ingredient, result, resultGroup) -> {
            List<GunSmithTableIngredient> ingredients = new ArrayList<>();
            for (Pair<Ingredient, Integer> pair : ingredient) {
                ingredients.add(new GunSmithTableIngredient(pair.getFirst(), pair.getSecond()));
            }
            return new GunSmithTableRecipe(recipeId, new GunSmithTableResult(result, resultGroup), ingredients);
        }));
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
