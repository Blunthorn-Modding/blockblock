package net.wouterb.blockblock.config;

import net.wouterb.blockblock.BlockBlock;
import net.wouterb.blockblock.util.Comment;
import net.wouterb.blockblock.util.ModLockManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

public class ModConfig {
    private static String objectIdPlaceholder = "{OBJECT}";
    private static String messageBreaking = "You do not have {OBJECT} unlocked!";
    private static String messagePlacement = "You do not have {OBJECT} unlocked!";
    private static String messageBlockInteraction = "You do not have {OBJECT} unlocked!";
    private static String messageEntityInteraction = "You do not have {OBJECT} unlocked!";
    private static String messageEntityDrop = "You do not have {OBJECT} unlocked!";
    private static String messageItemUsage = "You do not have {OBJECT} unlocked!";
    private static String messageRecipeUsage = "You do not have {OBJECT} unlocked!";

    @Comment("GENERAL\n# Whether the user will get the messages listed above or not")
    private static boolean displayMessagesToUser = true;
    private static boolean creativeBypassesRestrictions = true;

    public static String getMessage(ModLockManager.LockType lockType, String objectId) {
        return switch (lockType){
            case BREAKING -> messageBreaking.replace(objectIdPlaceholder, objectId);
            case PLACEMENT -> messagePlacement.replace(objectIdPlaceholder, objectId);
            case BLOCK_INTERACTION -> messageBlockInteraction.replace(objectIdPlaceholder, objectId);
            case ENTITY_INTERACTION -> messageEntityInteraction.replace(objectIdPlaceholder, objectId);
            case ENTITY_DROP -> messageEntityDrop.replace(objectIdPlaceholder, objectId);
            case ITEM_USAGE -> messageItemUsage.replace(objectIdPlaceholder, objectId);
            case CRAFTING_RECIPE -> messageRecipeUsage.replace(objectIdPlaceholder, objectId);
        };
    }

    public static boolean getCreativeBypassesRestrictions() {
        return creativeBypassesRestrictions;
    }

    public static boolean displayMessagesToUser() {
        return displayMessagesToUser;
    }

    //<editor-fold desc="Utility methods for ModConfig class">
    public static void load() {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(ModConfigManager.getConfigFile().getAbsolutePath())) {
            properties.load(fileInputStream);
            BlockBlock.LOGGER.info("Configuration loaded from file successfully.");
        } catch (Exception e) {
            BlockBlock.LOGGER.error("Error loading configuration from file: " + e.getMessage());
        }

        Field[] fields = ModConfig.class.getDeclaredFields();

        for (String propertyName : properties.stringPropertyNames()) {
            String propertyValue = properties.getProperty(propertyName);

            for (Field field : fields) {
                if (field.getName().equals(propertyName)) {
                    field.setAccessible(true);
                    try {
                        setFieldValue(field, propertyValue);
                    } catch (IllegalAccessException | NumberFormatException e) {
                        System.err.println("Error setting field: " + propertyName);
                    }
                    break;
                }
            }
        }
    }
    public static void generateDefaultConfig() {
        File configFile = ModConfigManager.getConfigFile();
        configFile.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write("# BlockBlock Config\n\n");
            writer.write("# MESSAGES\n# The value of 'objectIdPlaceholder' will get replaced with the object ID's.\n");

            Field[] fields = ModConfig.class.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);

                String fieldName = field.getName();

                if (field.isAnnotationPresent(Comment.class)){
                    Comment comment = field.getAnnotation(Comment.class);
                    writer.write("\n# " + comment.value() + "\n");
                }
                try {
                    writeProperty(writer, fieldName, field.get(null).toString());
                } catch (IllegalAccessException e) {
                    BlockBlock.LOGGER.error("Error accessing field: " + fieldName);
                }
            }
        } catch (IOException e) {
            BlockBlock.LOGGER.error("Error writing configuration to file: " + e.getMessage());
        }
    }

    private static void writeProperty(FileWriter writer, String propertyName, String propertyValue) throws IOException {
        writer.write(propertyName + "=" + propertyValue + "\n");
    }

    private static void setFieldValue(Field field, Object value) throws IllegalAccessException {
        try {
            field.setAccessible(true);
            var type = field.getType();
            if (type == int.class)
                field.setInt(null, (int) value);
            else if (type == boolean.class)
                field.setBoolean(null, Boolean.parseBoolean(value.toString()));
            else if (type == float.class)
                field.setFloat(null, (float) value);
            else
                field.set(null, value);
        } catch (ClassCastException e) {
            BlockBlock.LOGGER.error(String.format("Could not parse %s with value: '%s'! Using default value...", field.getName(), value));
        }
    }
    //</editor-fold>

}
