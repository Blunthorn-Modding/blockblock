package net.wouterb.blockblock.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.wouterb.blockblock.config.ModConfig;
import net.wouterb.blockblock.config.ModConfigManager;
import net.wouterb.blockblock.network.ClientLockSyncHandler;
import net.wouterb.blockblock.network.ConfigSyncHandler;
import net.wouterb.blockblock.util.IEntityDataSaver;

import java.util.Collection;

public class ResetCommand {

    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
//        LiteralArgumentBuilder<ServerCommandSource> command = CommandManager.literal("bb").requires(source -> source.hasPermissionLevel(2))
//                .then(CommandManager.literal("reset").requires(source -> source.hasPermissionLevel(2))
//                        .then(CommandManager.argument("targets", EntityArgumentType.entities())
//                                .then(CommandManager.argument("wipe", BoolArgumentType.bool())
//                        .executes(
//                        context -> run(context.getSource(),
//                                EntityArgumentType.getPlayers(context, "targets"),
//                                BoolArgumentType.getBool(context, "wipe")
//                        )
//                ))));
        LiteralArgumentBuilder<ServerCommandSource> command = CommandManager.literal("bb").requires(source -> source.hasPermissionLevel(2));
        var commandReset = CommandManager.literal("reset").requires(source -> source.hasPermissionLevel(2));
        var commandTargets = CommandManager.argument("targets", EntityArgumentType.entities());
        var commandWipe = CommandManager.argument("wipe", BoolArgumentType.bool());

        command.then(commandReset
                .then(commandTargets
                        .executes(context -> run(context.getSource(),
                                EntityArgumentType.getPlayers(context, "targets"),
                                false))
                        .then(commandWipe.executes(context -> run(context.getSource(),
                                EntityArgumentType.getPlayers(context, "targets"),
                                BoolArgumentType.getBool(context, "wipe"))))));

        serverCommandSourceCommandDispatcher.register(command);
    }

    public static int run(ServerCommandSource source, Collection<ServerPlayerEntity> targets, boolean wipe) {
        ServerPlayerEntity player = source.getPlayer();

        for (ServerPlayerEntity target : targets) {
            IEntityDataSaver dataSaver = (IEntityDataSaver) target;
            dataSaver.resetPersistentData(wipe);

            ClientLockSyncHandler.updateClient(target, dataSaver.getPersistentData());

            if (player != null) {
                if (wipe)
                    player.sendMessage(Text.of(String.format("Removed all restrictions for %s", target.getName().getString())));
                else
                    player.sendMessage(Text.of(String.format("Reset restrictions to default for %s", target.getName().getString())));
            }
        }

        return 1;
    }
}
