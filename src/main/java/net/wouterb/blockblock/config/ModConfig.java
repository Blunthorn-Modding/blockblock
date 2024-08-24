package net.wouterb.blockblock.config;

import net.wouterb.blockblock.util.ModLockManager;
import net.wouterb.blunthornapi.api.config.BlunthornConfig;
import net.wouterb.blunthornapi.api.config.BlankLine;
import net.wouterb.blunthornapi.api.config.Comment;
import net.wouterb.blunthornapi.api.config.StoreInConfig;

import java.nio.file.Path;

import static net.wouterb.blockblock.BlockBlock.MOD_ID;

public class ModConfig extends BlunthornConfig {
    @StoreInConfig
    private static String objectIdPlaceholder = "{OBJECT}";
    @StoreInConfig
    private static String messageBreaking = "You do not have {OBJECT} unlocked!";
    @StoreInConfig
    private static String messagePlacement = "You do not have {OBJECT} unlocked!";
    @StoreInConfig
    private static String messageBlockInteraction = "You do not have {OBJECT} unlocked!";
    @StoreInConfig
    private static String messageEntityInteraction = "You do not have {OBJECT} unlocked!";
    @StoreInConfig
    private static String messageEntityDrop = "You do not have {OBJECT} unlocked!";
    @StoreInConfig
    private static String messageItemUsage = "You do not have {OBJECT} unlocked!";
    @StoreInConfig
    private static String messageRecipeUsage = "You do not have {OBJECT} unlocked!";

    @BlankLine
    @Comment("GENERAL\n# Whether the user will get the messages listed above or not")
    @StoreInConfig
    private static boolean displayMessagesToUser = true;
    @Comment("If true, players in creative will not be affected by locked objects")
    @StoreInConfig
    private static boolean creativeBypassesRestrictions = true;
    @Comment("If true, a locked block in category 'breaking' will become unbreakable by mining with hand/tool")
    @StoreInConfig
    private static boolean breakingLockedPreventsBreaking = false;
    @Comment("This value determines how much longer it takes when trying to break a block that is locked.\n# Higher is slower. Value should be at least 0. Calculation: deltaBreakTime / lockedBreakTimeModifier")
    @StoreInConfig
    private static float lockedBreakTimeModifier = 5.0f;

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

    public static boolean getBreakingLockedPreventsBreaking() { return breakingLockedPreventsBreaking; }

    public static float getLockedBreakTimeModifier() {
        return lockedBreakTimeModifier;
    }

    public static boolean displayMessagesToUser() {
        return displayMessagesToUser;
    }


    public ModConfig() {
        this.filePath = Path.of(MOD_ID, MOD_ID + "_config.properties").toString();
        this.modId = MOD_ID;
        init();
    }
}
