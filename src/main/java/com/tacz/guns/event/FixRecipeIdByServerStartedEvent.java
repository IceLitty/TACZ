package com.tacz.guns.event;

import com.tacz.guns.GunMod;
import com.tacz.guns.crafting.GunSmithTableRecipe;
import com.tacz.guns.init.ModRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@EventBusSubscriber(modid = GunMod.MOD_ID)
public class FixRecipeIdByServerStartedEvent {

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        for (RecipeHolder<GunSmithTableRecipe> recipeHolder : event.getServer().getRecipeManager().getAllRecipesFor(ModRecipe.GUN_SMITH_TABLE_CRAFTING.get())) {
            recipeHolder.value().setId(recipeHolder.id());
        }
    }

}
