package net.wouterb.blockblock.util;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.wouterb.blockblock.command.*;
import net.wouterb.blockblock.config.BlockBlockConfig;
import net.wouterb.blockblock.config.ModConfigManager;
import net.wouterb.blunthornapi.api.Api;
import net.wouterb.blunthornapi.api.context.ActionContext;
import net.wouterb.blunthornapi.api.context.ItemActionContext;
import net.wouterb.blunthornapi.api.event.*;
import net.wouterb.blunthornapi.api.permission.Permission;

import static net.minecraft.client.resource.language.I18n.translate;
import static net.wouterb.blockblock.BlockBlock.MOD_ID;

public class ModRegistries {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(UnlockCommand::register);
        CommandRegistrationCallback.EVENT.register(LockCommand::register);
        CommandRegistrationCallback.EVENT.register(ReloadCommand::register);
        CommandRegistrationCallback.EVENT.register(ResetCommand::register);
        CommandRegistrationCallback.EVENT.register(GetCommand::register);
    }

    public static void registerConfigs() {
        BlockBlockConfig config = new BlockBlockConfig();
        Api.registerConfig(config);
        ModConfigManager.registerConfig();
        ModConfigManager.configId = config.getConfigId();
    }

    public static void registerEvents() {
        BlockBreakEvent.ATTACK.register(blockActionContext -> {
            ItemActionContext context = new ItemActionContext(blockActionContext);
            if (Permission.isObjectLocked(context, MOD_ID)) {
                sendLockedFeedbackToPlayer(context);
                return ActionResult.FAIL;
            }

            if (!BlockBlockConfig.getBreakingLockedPreventsBreaking())
                return ActionResult.PASS;

            if (Permission.isObjectLocked(blockActionContext, MOD_ID)) {
                sendLockedFeedbackToPlayer(blockActionContext);
                return ActionResult.FAIL;
            }

            return ActionResult.PASS;
        });

        BlockBreakEvent.BEFORE.register(blockActionContext -> {
            if (!BlockBlockConfig.getBreakingLockedPreventsBreaking())
                return ActionResult.PASS;

            if (Permission.isObjectLocked(blockActionContext, MOD_ID)) {
                sendLockedFeedbackToPlayer(blockActionContext);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        BlockBreakEvent.AFTER.register(blockActionContext -> {
            if (Permission.isObjectLocked(blockActionContext, MOD_ID)) {
                sendLockedFeedbackToPlayer(blockActionContext);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        BlockPlaceEvent.EVENT.register(blockActionContext -> {
            if (Permission.isObjectLocked(blockActionContext, MOD_ID)) {
                sendLockedFeedbackToPlayer(blockActionContext);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        BlockUseEvent.EVENT.register(blockActionContext -> {
            if (Permission.isObjectLocked(blockActionContext, MOD_ID)) {
                sendLockedFeedbackToPlayer(blockActionContext);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        ItemUseEvent.EVENT.register(itemActionContext -> {
            if (Permission.isObjectLocked(itemActionContext, MOD_ID)) {
                sendLockedFeedbackToPlayer(itemActionContext);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        EntityUseEvent.EVENT.register(entityActionContext -> {
            if (Permission.isObjectLocked(entityActionContext, MOD_ID)) {
                sendLockedFeedbackToPlayer(entityActionContext);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        ObjectCraftedEvent.EVENT.register(itemActionContext -> {
            if (Permission.isObjectLocked(itemActionContext, MOD_ID)) {
                sendLockedFeedbackToPlayer(itemActionContext);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        EntityItemDropEvent.EVENT.register(entityActionContext -> {
            if (Permission.isObjectLocked(entityActionContext, MOD_ID)) {
                sendLockedFeedbackToPlayer(entityActionContext);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });
    }

    public static void sendLockedFeedbackToPlayer(ActionContext context) {
        if (!BlockBlockConfig.displayMessagesToUser()) return;

        String message = translate("message.blockblock." + context.getLockType());
        message = message.replace(BlockBlockConfig.getObjectIdPlaceholder(), context.getObjectId());
        context.getPlayer().sendMessage(Text.of(message), true);
    }
}

