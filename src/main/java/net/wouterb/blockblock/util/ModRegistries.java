package net.wouterb.blockblock.util;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.util.ActionResult;
import net.wouterb.blockblock.command.LockCommand;
import net.wouterb.blockblock.command.ReloadCommand;
import net.wouterb.blockblock.command.ResetCommand;
import net.wouterb.blockblock.command.UnlockCommand;
import net.wouterb.blockblock.config.ModConfig;
import net.wouterb.blockblock.config.ModConfigManager;
import net.wouterb.blunthornapi.api.Api;
import net.wouterb.blunthornapi.api.event.*;
import net.wouterb.blunthornapi.api.permission.Permission;

import static net.wouterb.blockblock.BlockBlock.MOD_ID;

public class ModRegistries {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(UnlockCommand::register);
        CommandRegistrationCallback.EVENT.register(LockCommand::register);
        CommandRegistrationCallback.EVENT.register(ReloadCommand::register);
        CommandRegistrationCallback.EVENT.register(ResetCommand::register);
    }

    public static void registerConfigs() {
        Api.registerConfig(new ModConfig());
        ModConfigManager.registerConfig();
    }

    public static void registerEvents() {
        BlockBreakEvent.ATTACK.register(blockActionContext -> {
            if (Permission.isObjectLocked(blockActionContext, MOD_ID))
                return ActionResult.FAIL;
            return ActionResult.PASS;
        });

        BlockBreakEvent.BEFORE.register(blockActionContext -> {
            if (Permission.isObjectLocked(blockActionContext, MOD_ID))
                return ActionResult.FAIL;
            return ActionResult.PASS;
        });

        BlockBreakEvent.AFTER.register(blockActionContext -> {
            if (Permission.isObjectLocked(blockActionContext, MOD_ID))
                return ActionResult.FAIL;
            return ActionResult.PASS;
        });

        BlockPlaceEvent.EVENT.register(blockActionContext -> {
            if (Permission.isObjectLocked(blockActionContext, MOD_ID))
                return ActionResult.FAIL;
            return ActionResult.PASS;
        });

        BlockUseEvent.EVENT.register(blockActionContext -> {
            if (Permission.isObjectLocked(blockActionContext, MOD_ID))
                return ActionResult.FAIL;
            return ActionResult.PASS;
        });

        ItemUseEvent.EVENT.register(itemActionContext -> {
            if (Permission.isObjectLocked(itemActionContext, MOD_ID))
                return ActionResult.FAIL;
            return ActionResult.PASS;
        });

        EntityUseEvent.EVENT.register(entityActionContext -> {
            if (Permission.isObjectLocked(entityActionContext, MOD_ID))
                return ActionResult.FAIL;
            return ActionResult.PASS;
        });

        ObjectCraftedEvent.EVENT.register(itemActionContext -> {
            if (Permission.isObjectLocked(itemActionContext, MOD_ID))
                return ActionResult.FAIL;
            return ActionResult.PASS;
        });

        EntityItemDropEvent.EVENT.register(entityActionContext -> {
            if (Permission.isObjectLocked(entityActionContext, MOD_ID))
                return ActionResult.FAIL;
            return ActionResult.PASS;
        });
    }
}

