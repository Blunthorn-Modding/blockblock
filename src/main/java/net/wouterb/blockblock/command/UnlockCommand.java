package net.wouterb.blockblock.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.*;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
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

            if (lockType == LockType.ENTITY_DROP || lockType == LockType.ENTITY_INTERACTION) {
                commandUnlock.then(commandLockType.then(commandTarget
                        .then(CommandManager.argument("namespace:entity_id/tag", RegistryEntryPredicateArgumentType.registryEntryPredicate(commandRegistryAccess, RegistryKeys.ENTITY_TYPE))
                                .executes(context -> {
                                            try {
                                                return run(context.getSource(),
                                                        lockType,
                                                        EntityArgumentType.getPlayers(context, "targets"),
                                                        RegistryEntryPredicateArgumentType.getRegistryEntryPredicate(context, "namespace:entity_id/tag", RegistryKeys.ENTITY_TYPE));
                                            } catch (Exception e) {
                                                String[] args = context.getInput().split(" ");
                                                return run(context.getSource(),
                                                        lockType,
                                                        EntityArgumentType.getPlayers(context, "targets"),
                                                        args[args.length - 1]);
                                            }
                                        }
                                )
                        )
                ));
            } else if (lockType == LockType.ITEM_USAGE || lockType == LockType.CRAFTING_RECIPE) {
                commandUnlock.then(commandLockType.then(commandTarget
                        .then(CommandManager.argument("namespace:item_id/tag", RegistryEntryPredicateArgumentType.registryEntryPredicate(commandRegistryAccess, RegistryKeys.ITEM))
                                .executes(context -> {
                                            try {
                                                return run(context.getSource(),
                                                        lockType,
                                                        EntityArgumentType.getPlayers(context, "targets"),
                                                        RegistryEntryPredicateArgumentType.getRegistryEntryPredicate(context, "namespace:item_id/tag", RegistryKeys.ITEM));
                                            } catch (Exception e) {
                                                String[] args = context.getInput().split(" ");
                                                return run(context.getSource(),
                                                        lockType,
                                                        EntityArgumentType.getPlayers(context, "targets"),
                                                        args[args.length - 1]);
                                            }
                                        }
                                )
                        )
                ));
            } else {
                commandUnlock.then(commandLockType.then(commandTarget
                        .then(CommandManager.argument("namespace:block_id/tag", RegistryEntryPredicateArgumentType.registryEntryPredicate(commandRegistryAccess, RegistryKeys.BLOCK))
                                .executes(context -> {
                                    try {
                                        return run(context.getSource(),
                                                lockType,
                                                EntityArgumentType.getPlayers(context, "targets"),
                                                RegistryEntryPredicateArgumentType.getRegistryEntryPredicate(context, "namespace:block_id/tag", RegistryKeys.BLOCK));
                                    } catch (Exception e) {
                                        String[] args = context.getInput().split(" ");
                                        return run(context.getSource(),
                                                lockType,
                                                EntityArgumentType.getPlayers(context, "targets"),
                                                args[args.length - 1]);
                                    }
                                }
                            )
                        )
                ));
            }

            command.then(commandUnlock);
        }

        serverCommandSourceCommandDispatcher.register(command);
    }


    private static int run(ServerCommandSource source, LockType lockType, Collection<ServerPlayerEntity> targets, RegistryEntryPredicateArgumentType.EntryPredicate<?> objectOrTag) throws CommandSyntaxException {
        String id = objectOrTag.asString();

        return run(source, lockType, targets, id);
    }

    private static int run(ServerCommandSource source, LockType lockType, Collection<ServerPlayerEntity> targets, String objectOrTag) throws CommandSyntaxException {
        for (ServerPlayerEntity target : targets) {
            boolean success = Permission.unlockObject((IEntityDataSaver) target, objectOrTag, lockType, MOD_ID);

            if (success)
                source.sendFeedback(() -> Text.literal("Unlocking " + objectOrTag + " for " + target.getName().getString() + " in " + lockType), true);
            else
                source.sendFeedback(() -> Text.literal(target.getName().getString() + " already has " + objectOrTag + " unlocked in " + lockType), false);
        }
        return 1;
    }
}
