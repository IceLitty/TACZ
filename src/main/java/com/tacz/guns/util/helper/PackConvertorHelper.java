package com.tacz.guns.util.helper;

import com.google.gson.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

/**
 * 1.20.1 (format 15) -> 1.21.1 (format 48)
 */
public class PackConvertorHelper {

    public static void main(String[] args) {
        convertFolderFrom15To48("C:\\TACZ-Fork\\src\\main\\resources");
        convertFolderFrom15To48("C:\\TACZ-Fork\\src\\main\\resources\\assets\\tacz\\custom\\tacz_default_gun");
    }

    /**
     * @param gunPackFolder like /resources OR /resources/assets/tacz/custom/tacz_default_gun
     */
    public static void convertFolderFrom15To48(String gunPackFolder) {
        File _baseFolder = new File(gunPackFolder, "data");
        if (_baseFolder.exists()) {
            File[] files = _baseFolder.listFiles();
            if (files != null) {
                for (File dataPack : files) {
                    File[] dataPackEntries = dataPack.listFiles();
                    if (dataPackEntries != null) {
                        for (File dataPackEntry : dataPackEntries) {
                            if (dataPackEntry.isDirectory()) {
                                // 修改文件夹名称
                                String folderName = dataPackEntry.getName();
                                String newFolderName = null;
                                switch (folderName) {
                                    case "structures":
                                        newFolderName = "structure";
                                        break;
                                    case "advancements":
                                        newFolderName = "advancement";
                                        break;
                                    case "recipes":
                                        newFolderName = "recipe";
                                        break;
                                    case "loot_tables":
                                        newFolderName = "loot_table";
                                        break;
                                    case "predicates":
                                        newFolderName = "predicate";
                                        break;
                                    case "item_modifiers":
                                        newFolderName = "item_modifier";
                                        break;
                                    case "functions":
                                        newFolderName = "function";
                                        break;
                                    case "tags":
                                    {
                                        File[] tagsFolderList = dataPackEntry.listFiles();
                                        if (tagsFolderList != null) {
                                            for (File file : tagsFolderList) {
                                                if (file.isDirectory()) {
                                                    String tagSubFolderName = file.getName();
                                                    String newTagSubFolderName = null;
                                                    switch (tagSubFolderName) {
                                                        case "functions":
                                                            newTagSubFolderName = "function";
                                                            break;
                                                        case "items":
                                                            newTagSubFolderName = "item";
                                                            break;
                                                        case "blocks":
                                                            newTagSubFolderName = "block";
                                                            break;
                                                        case "entity_types":
                                                            newTagSubFolderName = "entity_type";
                                                            break;
                                                        case "fluids":
                                                            newTagSubFolderName = "fluid";
                                                            break;
                                                        case "game_events":
                                                            newTagSubFolderName = "game_event";
                                                            break;
                                                    }
                                                    if (newTagSubFolderName != null) {
                                                        File newTagSubFolder = new File(file.getParentFile(), newTagSubFolderName);
                                                        //noinspection ResultOfMethodCallIgnored
                                                        file.renameTo(newTagSubFolder);
                                                        System.out.println("renamed " + file.getAbsolutePath() + " to " + newTagSubFolder.getName());
                                                        file = newTagSubFolder;
                                                    }
                                                    // 暂时 不需要修改文件内容
                                                }
                                            }
                                        }
                                        break;
                                    }
                                }
                                if (newFolderName != null) {
                                    File newFolder = new File(dataPackEntry.getParentFile(), newFolderName);
                                    //noinspection ResultOfMethodCallIgnored
                                    dataPackEntry.renameTo(newFolder);
                                    System.out.println("renamed " + dataPackEntry.getAbsolutePath() + " to " + newFolder.getName());
                                    dataPackEntry = newFolder;
                                }
                                // recipe
                                if ("recipe".equals(dataPackEntry.getName())) {
                                    convertFolderFrom15To48InnerRecipe(dataPackEntry);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void convertFolderFrom15To48InnerRecipe(File baseFolder) {
        File[] entries = baseFolder.listFiles();
        if (entries != null) {
            for (File entry : entries) {
                if (entry.isDirectory()) {
                    convertFolderFrom15To48InnerRecipe(entry);
                } else if (entry.isFile()) {
                    try {
                        String json = Files.readString(entry.toPath());
                        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
                        boolean modified = false;
                        if (jsonObject.has("result")) {
                            JsonObject result = jsonObject.getAsJsonObject("result");
                            modified |= convertFrom15To48InnerRecipeResult(result);
                        }
                        if (jsonObject.has("ingredients")) {
                            for (JsonElement ingredient : jsonObject.getAsJsonArray("ingredients")) {
                                JsonObject _ing = ingredient.getAsJsonObject();
                                modified |= convertFrom15To48InnerRecipeIngredients(_ing);
                            }
                        }
                        if (jsonObject.has("key")) {
                            JsonObject keyObj = jsonObject.get("key").getAsJsonObject();
                            for (Map.Entry<String, JsonElement> keyEntry : keyObj.entrySet()) {
                                JsonObject _ing = keyEntry.getValue().getAsJsonObject();
                                modified |= convertFrom15To48InnerRecipeIngredients(_ing);
                            }
                        }
                        if (jsonObject.has("materials")) {
                            JsonArray materials = jsonObject.get("materials").getAsJsonArray();
                            for (JsonElement material : materials) {
                                JsonObject _material = material.getAsJsonObject();
                                if (_material.has("item")) {
                                    JsonObject _ing = _material.get("item").getAsJsonObject();
                                    modified |= convertFrom15To48InnerRecipeIngredients(_ing);
                                }
                            }
                        }
                        if (modified) {
                            Files.write(entry.toPath(), new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject).getBytes());
                            System.out.println("converted " + entry.getAbsolutePath());
                        }
                    } catch (IOException e) {
                        System.err.println("Error when convert file: " + entry.getAbsolutePath() + " : " + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * { "item": "tacz:ammo_box", "nbt": { "Level": 0 } } -> { "id": "tacz:ammo_box", "components": { "tacz:data": { "Level": 0 } } }
     */
    private static boolean convertFrom15To48InnerRecipeResult(JsonObject result) {
        boolean modified = false;
        if (result.has("item")) {
            JsonElement item = result.get("item");
            result.add("id", item);
            result.remove("item");
            modified = true;
        }
        // CAST TAG TO TACZ COMPONENTS ONLY !!!
        if (result.has("nbt")) {
            JsonObject nbt = result.getAsJsonObject("nbt");
            JsonObject components = new JsonObject();
            // need same as `com.tacz.guns.init.ModComponents.CUSTOM_DATA`
            components.add("tacz:data", nbt);
            result.add("components", components);
            result.remove("nbt");
            modified = true;
        }
        return modified;
    }

    /**
     * { "tag": "forge:xxx" } -> { "tag": "c:xxx" }
     */
    private static boolean convertFrom15To48InnerRecipeIngredients(JsonObject itemObject) {
        boolean modified = false;
        // not changed in 1.21.1
        // { "item": "minecraft:xxx" } -> { "id": "minecraft:xxx" }
//        if (itemObject.has("item")) {
//            JsonElement item = itemObject.get("item");
//            itemObject.add("id", item);
//            itemObject.remove("item");
//            modified = true;
//        }
        if (itemObject.has("tag")) {
            String tag = itemObject.getAsJsonPrimitive("tag").getAsString();
            modified = true;
            // may have more tag changes in `net.neoforged.neoforge.common.Tags`
            tag = switch (tag) {
                case "forge:glass" -> "c:glass_block";
                case "forge:leather" -> "c:leathers";
                case "forge:gunpowder" -> "c:gunpowders";
                default -> {
                    modified = false;
                    yield tag;
                }
            };
            if (tag.startsWith("forge:")) {
                tag = "c:" + tag.substring("forge:".length());
                modified = true;
            }
            itemObject.add("tag", new JsonPrimitive(tag));
        }
        return modified;
    }

    /**
     * { "function": "minecraft:copy_nbt", ... } -> { "function": "minecraft:copy_custom_data", ... }
     */
    private static boolean convertFrom15To48InnerLootTableFunctions(JsonObject funcObject) {
        // TODO loot_table/blocks transfer
        return false;
    }

}
