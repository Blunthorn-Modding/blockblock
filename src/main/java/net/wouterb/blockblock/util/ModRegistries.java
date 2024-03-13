package net.wouterb.blockblock.util;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.wouterb.blockblock.BlockBlock;
import net.wouterb.blockblock.command.LockCommand;
import net.wouterb.blockblock.command.ReloadCommand;
import net.wouterb.blockblock.command.UnlockCommand;
import net.wouterb.blockblock.config.LockedDefaultValues;
import net.wouterb.blockblock.config.ModConfig;
import net.wouterb.blockblock.config.ModConfigManager;
import net.wouterb.blockblock.network.ClientLockSyncHandler;
import net.wouterb.blockblock.network.ConfigSyncHandler;

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
        PlayerBlockBreakEvents.BEFORE.register(ModRegistries::onBlockBroken);
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
        ClientLockSyncHandler.updateClient(player, data);
        ConfigSyncHandler.updateClient(player);
    }

    /**
    Mainly a redundancy, if somehow the player manages to break a block with `breakingLockedPreventsBreaking` enabled
     */
    private static boolean onBlockBroken(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (!ModConfig.getBreakingLockedPreventsBreaking()) return true;

        if (player.isCreative() && ModConfig.getCreativeBypassesRestrictions()) return true;

        String blockId = Registries.BLOCK.getId(state.getBlock()).toString();

        ItemStack tool = player.getStackInHand(Hand.MAIN_HAND);
        String itemId = Registries.ITEM.getId(tool.getItem()).toString();

        // Breaking with tool lock
        if (((IPlayerPermissionHelper) player).isItemLocked(itemId, ModLockManager.LockType.ITEM_USAGE)) {
            return false;
        }

        // General block breaking lock
        if (((IPlayerPermissionHelper) player).isBlockLocked(blockId, ModLockManager.LockType.BREAKING))
            return false;

        return true;
    }

}

