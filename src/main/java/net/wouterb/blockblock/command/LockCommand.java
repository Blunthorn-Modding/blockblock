package net.wouterb.blockblock.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.RegistryEntryPredicateArgumentType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.wouterb.blockblock.util.IEntityDataSaver;
import net.wouterb.blockblock.util.ModLockManager.LockType;
import net.wouterb.blockblock.util.ModLockManager;


import java.util.Collection;



public class LockCommand {

    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        LiteralArgumentBuilder<ServerCommandSource> command = CommandManager.literal("bb");

        for (ModLockManager.LockType lockType : ModLockManager.LockType.values()) {
            var commandUnlock = CommandManager.literal("lock").requires(source -> source.hasPermissionLevel(2));
            var commandLockType = CommandManager.literal(lockType.toString()).requires(source -> source.hasPermissionLevel(2));
            var commandTarget = CommandManager.argument("targets", EntityArgumentType.entities());

            if (lockType == ModLockManager.LockType.ENTITY_DROP || lockType == ModLockManager.LockType.ENTITY_INTERACTION) {
                commandUnlock.then(commandLockType.then(commandTarget
                        .then(CommandManager.argument("namespace:entity_id/tag", RegistryEntryPredicateArgumentType.registryEntryPredicate(commandRegistryAccess, RegistryKeys.ENTITY_TYPE))
                                .executes(context -> run(context.getSource(),
                                        lockType,
                                        EntityArgumentType.getPlayers(context, "targets"),
                                        RegistryEntryPredicateArgumentType.getRegistryEntryPredicate(context, "namespace:entity_id/tag", RegistryKeys.ENTITY_TYPE))
                                )
                        )
                ));
            } else if (lockType == ModLockManager.LockType.ITEM_USAGE) {
                commandUnlock.then(commandLockType.then(commandTarget
                        .then(CommandManager.argument("namespace:item_id/tag", RegistryEntryPredicateArgumentType.registryEntryPredicate(commandRegistryAccess, RegistryKeys.ITEM))
                                .executes(context -> run(context.getSource(),
                                        lockType,
                                        EntityArgumentType.getPlayers(context, "targets"),
                                        RegistryEntryPredicateArgumentType.getRegistryEntryPredicate(context, "namespace:item_id/tag", RegistryKeys.ITEM))
                                )
                        )
                ));
            } else if (lockType == ModLockManager.LockType.CRAFTING_RECIPE) {
                commandUnlock.then(commandLockType.then(commandTarget
                        .then(CommandManager.argument("namespace:recipe_id/tag", RegistryEntryPredicateArgumentType.registryEntryPredicate(commandRegistryAccess, RegistryKeys.RECIPE_TYPE))
                                .executes(context -> run(context.getSource(),
                                        lockType,
                                        EntityArgumentType.getPlayers(context, "targets"),
                                        RegistryEntryPredicateArgumentType.getRegistryEntryPredicate(context, "namespace:recipe_id/tag", RegistryKeys.RECIPE_TYPE))
                                )
                        )
                ));
            } else {
                commandUnlock.then(commandLockType.then(commandTarget
                        .then(CommandManager.argument("namespace:block_id/tag", RegistryEntryPredicateArgumentType.registryEntryPredicate(commandRegistryAccess, RegistryKeys.BLOCK))
                                .executes(context -> run(context.getSource(),
                                        lockType,
                                        EntityArgumentType.getPlayers(context, "targets"),
                                        RegistryEntryPredicateArgumentType.getRegistryEntryPredicate(context, "namespace:block_id/tag", RegistryKeys.BLOCK))
                                )
                        )
                ));
            }

            command.then(commandUnlock);
        }

        serverCommandSourceCommandDispatcher.register(command);
    }


    private static int run(ServerCommandSource source, ModLockManager.LockType lockType, Collection<ServerPlayerEntity> targets, RegistryEntryPredicateArgumentType.EntryPredicate<?> objectOrTag) throws CommandSyntaxException {
        for (ServerPlayerEntity target : targets) {
            String id = objectOrTag.asString();
            ModLockManager.lock((IEntityDataSaver) target, id, lockType, source);
        }
        return 1;
    }
}
