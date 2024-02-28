package net.wouterb.blockblock.util;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.wouterb.blockblock.BlockBlock;
import net.wouterb.blockblock.command.LockCommand;
import net.wouterb.blockblock.command.ReloadCommand;
import net.wouterb.blockblock.command.UnlockCommand;
import net.wouterb.blockblock.config.LockedDefaultValues;
import net.wouterb.blockblock.config.ModConfigManager;

public class ModRegistries {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(UnlockCommand::register);
        CommandRegistrationCallback.EVENT.register(LockCommand::register);
        CommandRegistrationCallback.EVENT.register(ReloadCommand::register);
    }

    public static void registerConfigs() {
        ModConfigManager.registerConfig();
    }

    public static void registerEvents() {
        ServerPlayConnectionEvents.JOIN.register(ModRegistries::onPlayerJoin);
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            NbtCompound oldNbt = ((IEntityDataSaver)oldPlayer).getPersistentData();
            ((IEntityDataSaver)newPlayer).setPersistentData(oldNbt);
        });
    }

    private static void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server){
        ServerPlayerEntity player = handler.getPlayer();
        NbtCompound data = ((IEntityDataSaver) player).getPersistentData();

        if (data.isEmpty()){
            BlockBlock.LOGGER.info("Player without BlockBlock data joined, assigning default values...");
            LockedDefaultValues defaultValues = ModConfigManager.getDefaultLockedValues();
            for (ModLockManager.LockType lockType : ModLockManager.LockType.values()){
                String[] locked = defaultValues.getFieldByString(lockType.toString());
                NbtList nbtList = new NbtList();
                for (String id : locked)
                    nbtList.add(NbtString.of(id));

                data.put(lockType.toString(), nbtList);
            }
        }
    }

}

