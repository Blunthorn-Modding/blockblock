package net.wouterb.blockblock.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.wouterb.blockblock.config.ModConfigManager;
import net.wouterb.blunthornapi.api.Util;
import net.wouterb.blunthornapi.api.config.ConfigManager;


public class ReloadCommand {

    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        LiteralArgumentBuilder<ServerCommandSource> command = CommandManager.literal("bb").requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("reload").requires(source -> source.hasPermissionLevel(2)).executes(
                        context -> run(context.getSource())
                ));
        serverCommandSourceCommandDispatcher.register(command);
    }

    public static int run(ServerCommandSource source) {
        ModConfigManager.registerConfig();
        ConfigManager.getConfig(ModConfigManager.configId).load();
        ServerPlayerEntity player = source.getPlayer();
        if (player != null)
            player.sendMessage(Text.of("Reloaded the BlockBlock config!"));

        MinecraftServer server = source.getServer();
        var players = server.getPlayerManager().getPlayerList();
        for (ServerPlayerEntity serverPlayer : players){
            Util.updateAllClientConfigs(serverPlayer);
        }

        return 1;
    }
}
