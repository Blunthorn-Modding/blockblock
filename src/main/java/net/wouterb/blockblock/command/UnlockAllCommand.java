package net.wouterb.blockblock.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryEntryPredicateArgumentType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.wouterb.blockblock.config.BlockBlockConfig;
import net.wouterb.blunthornapi.api.permission.LockType;
import net.wouterb.blunthornapi.api.permission.Permission;
import net.wouterb.blunthornapi.core.data.IEntityDataSaver;

import java.util.Collection;

import static net.wouterb.blockblock.BlockBlock.MOD_ID;

public class UnlockAllCommand {
    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        LiteralArgumentBuilder<ServerCommandSource> command = CommandManager.literal("bb").requires(source -> source.hasPermissionLevel(2));
        var commandUnlock = CommandManager.literal("unlock").requires(source -> source.hasPermissionLevel(2));
        var commandAll = CommandManager.literal("all");
        var commandTarget = CommandManager.argument("targets", EntityArgumentType.entities());
        var commandForce = CommandManager.literal("-f");

        commandUnlock.then(commandAll.then(commandTarget
            .then(CommandManager.argument("namespace:object_id/tag", RegistryEntryPredicateArgumentType.registryEntryPredicate(commandRegistryAccess, RegistryKeys.ITEM))
                .executes(context -> run(
                    context.getSource(),
                    EntityArgumentType.getPlayers(context, "targets"),
                    RegistryEntryPredicateArgumentType.getRegistryEntryPredicate(context, "namespace:object_id/tag", RegistryKeys.ITEM))
                )
            )
            .then(
                commandForce.then(
                    CommandManager.argument("namespace:object_id/tag", StringArgumentType.greedyString())
                        .executes(
                            context -> run(
                                context.getSource(),
                                EntityArgumentType.getPlayers(context, "targets"),
                                StringArgumentType.getString(context, "namespace:object_id/tag")
                            )
                        )
                    )
                )
            )
        );

        command.then(commandUnlock);
        serverCommandSourceCommandDispatcher.register(command);
    }

    private static int run(ServerCommandSource source, Collection<ServerPlayerEntity> targets, RegistryEntryPredicateArgumentType.EntryPredicate<?> objectOrTag) throws CommandSyntaxException {
        String id = objectOrTag.asString();

        return run(source, targets, id);
    }

    private static int run(ServerCommandSource source, Collection<ServerPlayerEntity> targets, String id) throws CommandSyntaxException {
        for (ServerPlayerEntity target : targets) {
            for (LockType lockType : LockType.values()) {
                Permission.unlockObject((IEntityDataSaver) target, id, lockType, MOD_ID);
            }
            source.sendFeedback(() -> Text.literal("Unlocking " + id + " for " + target.getName().getString() + " in all categories"), BlockBlockConfig.getBroadcastCommandsToOperators());
        }
        return 1;
    }
}
