package net.wouterb.blockblock.util;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.wouterb.blockblock.command.LockCommand;
import net.wouterb.blockblock.command.ReloadCommand;
import net.wouterb.blockblock.command.UnlockCommand;

public class ModRegistries {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(UnlockCommand::register);
        CommandRegistrationCallback.EVENT.register(LockCommand::register);
        CommandRegistrationCallback.EVENT.register(ReloadCommand::register);
    }

}

