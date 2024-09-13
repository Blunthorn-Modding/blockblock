package net.wouterb.blockblock.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.wouterb.blockblock.BlockBlock;
import net.wouterb.blunthornapi.api.permission.LockType;
import net.wouterb.blunthornapi.core.data.IEntityDataSaver;

import java.util.Collection;

public class GetCommand {

    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        LiteralArgumentBuilder<ServerCommandSource> command = CommandManager.literal("bb").requires(source -> source.hasPermissionLevel(2));

        for (LockType lockType : LockType.values()) {
            var commandGet = CommandManager.literal("get").requires(source -> source.hasPermissionLevel(2));
            var commandLockType = CommandManager.literal(lockType.toString()).requires(source -> source.hasPermissionLevel(2));
            var commandTarget = CommandManager.argument("targets", EntityArgumentType.entities());

            commandGet.then(
                commandLockType.then(
                    commandTarget.executes(
                        context -> run(
                            context.getSource(),
                            lockType,
                            EntityArgumentType.getPlayers(context, "targets"),
                            null
                        )
                    )
                .then(CommandManager.argument("search_query", StringArgumentType.greedyString())
                    .executes(
                        context -> run(
                            context.getSource(),
                            lockType,
                            EntityArgumentType.getPlayers(context, "targets"),
                            StringArgumentType.getString(context, "search_query")
                        )
                    )
                )
            ));

            command.then(commandGet);
        }

    serverCommandSourceCommandDispatcher.register(command);
    }


    private static int run(ServerCommandSource source, LockType lockType, Collection<ServerPlayerEntity> targets, String searchQuery) throws CommandSyntaxException {
        if (source.getPlayer() == null)
            return 1;

        for (ServerPlayerEntity target : targets) {
            source.getPlayer().sendMessage(Text.of("\nRetrieving: " + lockType.toString() + " for: " + target.getName().getString()));
            NbtCompound nbt = ((IEntityDataSaver) target).blunthornapi$getPersistentData(BlockBlock.MOD_ID);
            String nbtKey = lockType.toString();
            NbtList locked = nbt.getList(nbtKey, NbtElement.STRING_TYPE);
            for (NbtElement element : locked){
                String id = element.asString();
                if (searchQuery == null || id.contains(searchQuery)) {
                    source.getPlayer().sendMessage(Text.of(id));
                }
            }
        }
        return 1;
    }
}
