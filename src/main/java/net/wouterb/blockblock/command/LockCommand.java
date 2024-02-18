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
import net.wouterb.blockblock.BlockBlock;
import net.wouterb.blockblock.util.IEntityDataSaver;
import net.wouterb.blockblock.util.ModLockManager.LOCK_TYPES;
import net.wouterb.blockblock.util.ModLockManager;


import java.util.Collection;



public class LockCommand {

    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        LiteralArgumentBuilder<ServerCommandSource> command = CommandManager.literal("bb");
        for(LOCK_TYPES lockType : LOCK_TYPES.values()){
                command.requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("lock")
                    .then(CommandManager.literal(lockType.toString())
                    .then(CommandManager.argument("targets", EntityArgumentType.entities())
                    .then(CommandManager.argument("block_or_tag", ItemStackArgumentType.itemStack(commandRegistryAccess))
                        .executes(context -> run(context.getSource(),
                            lockType,
                            EntityArgumentType.getPlayers(context, "targets"),
                            ItemStackArgumentType.getItemStackArgument(context, "block_or_tag"))
                        )
                    )
                    )
                    )
                );
        }

        serverCommandSourceCommandDispatcher.register(command);
    }

    private static int run(ServerCommandSource source, LOCK_TYPES lockType, Collection<ServerPlayerEntity> targets, ItemStackArgument blockOrTag) throws CommandSyntaxException {
        // Implementation of your command logic
        for (ServerPlayerEntity target : targets) {
            String block_id = blockOrTag.asString();
            ModLockManager.lock((IEntityDataSaver) target, block_id, lockType, source);
        }
        return 1; // Return command result
    }
}
