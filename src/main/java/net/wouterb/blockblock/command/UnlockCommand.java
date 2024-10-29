package net.wouterb.blockblock.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.*;
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

public class UnlockCommand {
    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        LiteralArgumentBuilder<ServerCommandSource> command = CommandManager.literal("bb").requires(source -> source.hasPermissionLevel(2));

        for (LockType lockType : LockType.values()) {
            var commandUnlock = CommandManager.literal("unlock").requires(source -> source.hasPermissionLevel(2));
            var commandLockType = CommandManager.literal(lockType.toString()).requires(source -> source.hasPermissionLevel(2));
            var commandTarget = CommandManager.argument("targets", EntityArgumentType.entities());
            var commandForce = CommandManager.literal("-f");

            if (lockType == LockType.ENTITY_DROP || lockType == LockType.ENTITY_INTERACTION) {
                commandUnlock.then(commandLockType.then(commandTarget
                    .then(CommandManager.argument("namespace:entity_id/tag", RegistryEntryPredicateArgumentType.registryEntryPredicate(commandRegistryAccess, RegistryKeys.ENTITY_TYPE))
                        .executes(context -> run(context.getSource(),
                            lockType,
                            EntityArgumentType.getPlayers(context, "targets"),
                            RegistryEntryPredicateArgumentType.getRegistryEntryPredicate(context, "namespace:entity_id/tag", RegistryKeys.ENTITY_TYPE))
                        )
                    )
                    .then(
                        commandForce.then(
                            CommandManager.argument("namespace:entity_id/tag", StringArgumentType.greedyString())
                                .executes(
                                    context -> run(
                                        context.getSource(),
                                        lockType,
                                        EntityArgumentType.getPlayers(context, "targets"),
                                        StringArgumentType.getString(context, "namespace:entity_id/tag")
                                    )
                                )
                            )
                        )
                    )
                );
            } else if (lockType == LockType.ITEM_USAGE) {
                commandUnlock.then(commandLockType.then(commandTarget
                    .then(CommandManager.argument("namespace:item_id/tag", RegistryEntryPredicateArgumentType.registryEntryPredicate(commandRegistryAccess, RegistryKeys.ITEM))
                        .executes(context -> run(context.getSource(),
                            lockType,
                            EntityArgumentType.getPlayers(context, "targets"),
                            RegistryEntryPredicateArgumentType.getRegistryEntryPredicate(context, "namespace:item_id/tag", RegistryKeys.ITEM))
                        )
                    )
                    .then(
                        commandForce.then(
                            CommandManager.argument("namespace:item_id/tag", StringArgumentType.greedyString())
                                .executes(
                                    context -> run(
                                        context.getSource(),
                                        lockType,
                                        EntityArgumentType.getPlayers(context, "targets"),
                                        StringArgumentType.getString(context, "namespace:item_id/tag")
                                    )
                                )
                            )
                        )
                    )
                );
            } else if (lockType == LockType.CRAFTING_RECIPE) {
                commandUnlock.then(commandLockType.then(commandTarget
                    .then(CommandManager.argument("namespace:recipe_id/tag", RegistryEntryPredicateArgumentType.registryEntryPredicate(commandRegistryAccess, RegistryKeys.ITEM))
                        .executes(context -> run(context.getSource(),
                            lockType,
                            EntityArgumentType.getPlayers(context, "targets"),
                            RegistryEntryPredicateArgumentType.getRegistryEntryPredicate(context, "namespace:recipe_id/tag", RegistryKeys.ITEM))
                        )

                    )
                    .then(
                        commandForce.then(
                            CommandManager.argument("namespace:recipe_id/tag", StringArgumentType.greedyString())
                                .executes(
                                    context -> run(
                                            context.getSource(),
                                            lockType,
                                            EntityArgumentType.getPlayers(context, "targets"),
                                            StringArgumentType.getString(context, "namespace:recipe_id/tag")
                                    )
                                )
                            )
                        )
                    )
                );
            } else {
                commandUnlock.then(commandLockType.then(commandTarget
                    .then(CommandManager.argument("namespace:block_id/tag", RegistryEntryPredicateArgumentType.registryEntryPredicate(commandRegistryAccess, RegistryKeys.BLOCK))
                        .executes(context -> run(
                            context.getSource(),
                            lockType,
                            EntityArgumentType.getPlayers(context, "targets"),
                            RegistryEntryPredicateArgumentType.getRegistryEntryPredicate(context, "namespace:block_id/tag", RegistryKeys.BLOCK))
                        )
                    )
                    .then(
                        commandForce.then(
                            CommandManager.argument("namespace:block_id/tag", StringArgumentType.greedyString())
                                .executes(
                                    context -> run(
                                        context.getSource(),
                                        lockType,
                                        EntityArgumentType.getPlayers(context, "targets"),
                                        StringArgumentType.getString(context, "namespace:block_id/tag")
                                    )
                                )
                            )
                        )
                    )
                );
            }
            command.then(commandUnlock);
        }

        serverCommandSourceCommandDispatcher.register(command);
    }


    private static int run(ServerCommandSource source, LockType lockType, Collection<ServerPlayerEntity> targets, RegistryEntryPredicateArgumentType.EntryPredicate<?> objectOrTag) throws CommandSyntaxException {
        String id = objectOrTag.asString();

        return run(source, lockType, targets, id);
    }

    private static int run(ServerCommandSource source, LockType lockType, Collection<ServerPlayerEntity> targets, String id) throws CommandSyntaxException {
        for (ServerPlayerEntity target : targets) {
            boolean success = Permission.unlockObject((IEntityDataSaver) target, id, lockType, MOD_ID);

            if (success)
                source.sendFeedback(() -> Text.literal("Unlocking " + id + " for " + target.getName().getString() + " in " + lockType), BlockBlockConfig.getBroadcastCommandsToOperators());
            else
                source.sendFeedback(() -> Text.literal(target.getName().getString() + " already has " + id + " unlocked in " + lockType), false);
        }
        return 1;
    }
}
