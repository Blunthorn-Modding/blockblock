package net.wouterb.blockblock.config;

import net.minecraft.entity.player.PlayerEntity;
import net.wouterb.blunthornapi.api.config.BlunthornConfig;
import net.wouterb.blunthornapi.api.config.Comment;
import net.wouterb.blunthornapi.api.config.StoreInConfig;

import java.nio.file.Path;

import static net.wouterb.blockblock.BlockBlock.MOD_ID;

public class BlockBlockConfig extends BlunthornConfig {
    @Comment("BlockBlock Config\n# Whether the user will get notified when they attempt a locked action")
    @StoreInConfig
    private static boolean displayMessagesToUser = true;
    @Comment("If true, players in creative will not be affected by locked objects")
    @StoreInConfig
    private static boolean creativeBypassesRestrictions = true;
    @Comment("If true, operators will not be affected by locked objects")
    @StoreInConfig
    private static boolean operatorBypassesRestrictions = false;
    @Comment("If true, commands will be broadcast to all operators")
    @StoreInConfig
    private static boolean broadcastCommandsToOperators = true;
    @Comment("If true, a locked block in category 'breaking' will become unbreakable by mining with hand/tool")
    @StoreInConfig
    private static boolean breakingLockedPreventsBreaking = false;
    @StoreInConfig
    @Comment("The placeholder used in the lang files. Only change if you are also adding/replacing lang files.")
    private static String objectIdPlaceholder = "{OBJECT}";


    public static boolean getCreativeBypassesRestrictions() {
        return creativeBypassesRestrictions;
    }

    public static boolean getOperatorBypassesRestrictions() {
        return operatorBypassesRestrictions;
    }

    public static boolean getBreakingLockedPreventsBreaking() { return breakingLockedPreventsBreaking; }

    public static String getObjectIdPlaceholder() {
        return objectIdPlaceholder;
    }

    public static boolean displayMessagesToUser() {
        return displayMessagesToUser;
    }

    public static boolean getBroadcastCommandsToOperators() {
        return broadcastCommandsToOperators;
    }

    @Override
    public boolean isPlayerBypassingRestrictions(PlayerEntity player) {
        if (getOperatorBypassesRestrictions()) {
            if (player.hasPermissionLevel(4))
                return true;
        }

        if (getCreativeBypassesRestrictions()) {
            if (player.isCreative()) {
                return true;
            }
        }
        return false;
    }

    public BlockBlockConfig() {
        this.filePath = Path.of(MOD_ID, MOD_ID + "_config.properties").toString();
        this.modId = MOD_ID;
        init();
    }
}
