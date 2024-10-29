package net.wouterb.blockblock.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.wouterb.blockblock.config.BlockBlockConfig;
import net.wouterb.blunthornapi.core.data.IEntityDataSaver;
import net.wouterb.blunthornapi.core.network.PermissionSyncHandler;

import java.util.Collection;

import static net.wouterb.blockblock.BlockBlock.MOD_ID;

public class ResetCommand {

    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
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
            dataSaver.resetPersistentData(MOD_ID, wipe);

            PermissionSyncHandler.updateAllClientPermissions(target);

            if (player != null) {
                if (wipe)
                    source.sendFeedback(() -> Text.literal("Removed all restrictions for " + target.getName().getString()), BlockBlockConfig.getBroadcastCommandsToOperators());
                else
                    source.sendFeedback(() -> Text.literal("Reset restrictions to default for " + target.getName().getString()), BlockBlockConfig.getBroadcastCommandsToOperators());
            }
        }

        return 1;
    }
}
