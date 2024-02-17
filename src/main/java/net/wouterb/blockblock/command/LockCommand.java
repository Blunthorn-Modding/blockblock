package net.wouterb.blockblock.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.wouterb.blockblock.util.IEntityDataSaver;
import net.wouterb.blockblock.util.LockedData;

import java.util.Collection;

public class LockCommand {
    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        LiteralArgumentBuilder<ServerCommandSource> command = CommandManager.literal("bb")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("lock")
                        .then(CommandManager.argument("targets", EntityArgumentType.entities())
                                .then(CommandManager.argument("block_or_tag", ItemStackArgumentType.itemStack(commandRegistryAccess))
                                        .executes(context -> run(context.getSource(),
                                                EntityArgumentType.getPlayers(context, "targets"),
                                                ItemStackArgumentType.getItemStackArgument(context, "block_or_tag"))))));

        serverCommandSourceCommandDispatcher.register(command);
    }

    private static int run(ServerCommandSource source, Collection<ServerPlayerEntity> targets, ItemStackArgument blockOrTag) throws CommandSyntaxException {
        // Implementation of your command logic
        for (ServerPlayerEntity target : targets) {
            String block_id = blockOrTag.asString();
            source.sendFeedback(() -> Text.literal("Locking " + block_id + " for " + target.getName().getString()), false);

            LockedData.lockBlock((IEntityDataSaver) target, block_id);
        }
        return 1; // Return command result
    }
}
