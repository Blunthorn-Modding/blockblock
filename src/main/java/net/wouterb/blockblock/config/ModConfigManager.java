package net.wouterb.blockblock.config;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.wouterb.blockblock.BlockBlock;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfigManager {
    private static final Path CONFIG_DIR = Path.of(String.valueOf(FabricLoader.getInstance().getConfigDir()), BlockBlock.MOD_ID);
    private static final String CONFIG_FILE_NAME = BlockBlock.MOD_ID + "_config.properties";
    private static final String DEFAULT_VALUES_FILE_NAME = BlockBlock.MOD_ID + "_default_values.json";

    private static File configFile;
    private static File defaultValuesFile;

    private static ModConfig modConfig;
    private static LockedDefaultValues lockedDefaultValues;


    public static File getConfigFile() {
        return configFile;
    }


    public static void registerConfig() {
        Gson gson = new Gson();

//        Path configPath = Path.of(String.valueOf(CONFIG_DIR), CONFIG_FILE_NAME);
//        configFile = configPath.toFile();
//        if (!configFile.exists()) {
//            BlockBlock.LOGGER.info("No config found, generating one...");
//            ModConfig.generateDefaultConfig();
//        } else {
//            ModConfig.load();
//        }

        Path defaultValuesPath = Path.of(String.valueOf(CONFIG_DIR), DEFAULT_VALUES_FILE_NAME);
        defaultValuesFile = defaultValuesPath.toFile();
        if (!defaultValuesFile.exists()){
            BlockBlock.LOGGER.info("No default values file found, generating one...");
            generateDefaultValuesFile();
        } else {
            try {
                FileReader reader = new FileReader(defaultValuesFile);
                String json = JsonParser.parseReader(reader).toString();
                lockedDefaultValues = gson.fromJson(json, LockedDefaultValues.class);
            } catch (FileNotFoundException e) {
                generateDefaultValuesFile();
                throw new RuntimeException(e);
            }
        }
    }

    public static LockedDefaultValues getDefaultLockedValues() {
        return lockedDefaultValues;
    }


    private static void generateDefaultValuesFile() {
        try {
            Files.createFile(defaultValuesFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        lockedDefaultValues = new LockedDefaultValues();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(lockedDefaultValues);
        try (FileWriter writer = new FileWriter(defaultValuesFile.toString())) {
            writer.write(json);
        } catch (IOException e) {
            BlockBlock.LOGGER.error(e.toString());
        }
    }
}
