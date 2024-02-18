package net.wouterb.blockblock.config;

public class LockedDefaultValues {
    public final String[] breaking;
    public final String[] block_interaction;
    public final String[] entity_interaction;
    public final String[] item_usage;
    public final String[] crafting_recipe;

    public LockedDefaultValues(){
        breaking = new String[]{};
        block_interaction = new String[]{};
        entity_interaction = new String[]{};
        item_usage = new String[]{};
        crafting_recipe = new String[]{};
    }

    public LockedDefaultValues(String[] breaking, String[] block_interaction, String[] entityInteraction, String[] itemUsage, String[] craftingRecipe) {
        this.breaking = breaking;
        this.block_interaction = block_interaction;
        this.entity_interaction = entityInteraction;
        this.item_usage = itemUsage;
        this.crafting_recipe = craftingRecipe;
    }

    public String[] getFieldByString(String propertyName) {
        return switch (propertyName) {
            case "breaking" -> breaking;
            case "block_interaction" -> block_interaction;
            case "entity_interaction" -> entity_interaction;
            case "item_usage" -> item_usage;
            case "crafting_recipe" -> crafting_recipe;
            default -> throw new IllegalArgumentException("Invalid property name: " + propertyName);
        };
    }
}