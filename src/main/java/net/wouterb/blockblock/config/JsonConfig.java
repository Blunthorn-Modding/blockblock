package net.wouterb.blockblock.config;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.wouterb.blockblock.BlockBlock;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JsonConfig {
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir();
    private static final String CONFIG_FILE_NAME = "blockblock_locked_blocks_default.json";

    private static File file;

    public static void registerConfig() {
        Path configPath = Path.of(String.valueOf(CONFIG_DIR), CONFIG_FILE_NAME);
        file = configPath.toFile();
        if (!file.exists()){
            BlockBlock.LOGGER.info("No config found, generating one...");
            try {
                generateDefaultConfig(configPath);
            } catch (IOException e) {
                BlockBlock.LOGGER.error(e.toString());
            }
        }
    }

    public static List<String> getConfigData() {
        List<String> configData = new ArrayList<>();
        try (FileReader reader = new FileReader(file)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray valuesArray = jsonObject.getAsJsonArray("values");
            if (valuesArray != null) {
                for (JsonElement element : valuesArray) {
                    configData.add(element.getAsString());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return configData;
    }

    public static void generateDefaultConfig(Path configPath) throws IOException {
        File file = configPath.toFile();

        file.getParentFile().mkdirs();
        Files.createFile(configPath);

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("values", ConfigDefaultValues.getDefaultLockedBlocks());

        try (FileWriter writer = new FileWriter(String.valueOf(configPath))){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(jsonObject, writer);

        } catch (IOException e){
            BlockBlock.LOGGER.error(e.toString());
        }
    }
}
