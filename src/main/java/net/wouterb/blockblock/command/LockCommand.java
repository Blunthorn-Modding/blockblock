package net.wouterb.blockblock.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
        for(LockType lockType : LockType.values()){
                command.requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("lock")
                    .then(CommandManager.literal(lockType.toString())
                    .then(CommandManager.argument("targets", EntityArgumentType.entities())
                    .then(CommandManager.argument("namespace:id/tag", IdentifierArgumentType.identifier())
                        .executes(context -> run(context.getSource(),
                            lockType,
                            EntityArgumentType.getPlayers(context, "targets"),
                            IdentifierArgumentType.getIdentifier(context, "namespace:id/tag"))
                        )
                    )
                    )
                    )
                );
        }

        serverCommandSourceCommandDispatcher.register(command);
    }

    private static int run(ServerCommandSource source, LockType lockType, Collection<ServerPlayerEntity> targets, Identifier blockOrTag) throws CommandSyntaxException {
        for (ServerPlayerEntity target : targets) {
            String block_id = blockOrTag.toString();
            ModLockManager.lock((IEntityDataSaver) target, block_id, lockType, source);
        }
        return 1;
    }
}
