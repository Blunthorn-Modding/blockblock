package net.wouterb.blockblock.util;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.wouterb.blockblock.BlockBlock;
import net.wouterb.blockblock.command.LockCommand;
import net.wouterb.blockblock.command.ReloadCommand;
import net.wouterb.blockblock.command.ResetCommand;
import net.wouterb.blockblock.command.UnlockCommand;
import net.wouterb.blockblock.config.ModConfig;
import net.wouterb.blockblock.config.ModConfigManager;
import net.wouterb.blockblock.network.ClientLockSyncHandler;
import net.wouterb.blockblock.network.ConfigSyncHandler;
import net.wouterb.blockblock.util.mixinhelpers.ItemUsageMixinHelper;

public class ModRegistries {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(UnlockCommand::register);
        CommandRegistrationCallback.EVENT.register(LockCommand::register);
        CommandRegistrationCallback.EVENT.register(ReloadCommand::register);
        CommandRegistrationCallback.EVENT.register(ResetCommand::register);
    }

    public static void registerConfigs() {
        ModConfigManager.registerConfig();
    }

    public static void registerEvents() {
        ServerPlayConnectionEvents.JOIN.register(ModRegistries::onPlayerJoin);
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            NbtCompound oldNbt = ((IEntityDataSaver)oldPlayer).getPersistentData();
            ((IEntityDataSaver)newPlayer).setPersistentData(oldNbt);
            ConfigSyncHandler.updateClient(newPlayer);
        });
        PlayerBlockBreakEvents.BEFORE.register(ModRegistries::onBlockBroken);
        ServerEntityEvents.EQUIPMENT_CHANGE.register((livingEntity, equipmentSlot, previousStack, currentStack) -> {
            if (!(livingEntity instanceof PlayerEntity player)) return;

            if (ItemUsageMixinHelper.isItemLocked(player, currentStack) && equipmentSlot.isArmorSlot()){
                System.out.println("Cannot equip item!");
                PlayerInventory inventory = player.getInventory();
                System.out.println(previousStack.getName());
                inventory.armor.set(equipmentSlot.getEntitySlotId(), previousStack);
                int slot = inventory.getEmptySlot();
                if (slot == -1)
                    player.dropItem(currentStack, true);
                else
                    inventory.setStack(slot, currentStack);

                inventory.updateItems();
            }

        });
    }

    private static void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server){
        ServerPlayerEntity player = handler.getPlayer();
        NbtCompound data = ((IEntityDataSaver) player).getPersistentData();

        if (data.isEmpty()){
            BlockBlock.LOGGER.info("Player without BlockBlock data joined, assigning default values...");
            ((IEntityDataSaver) player).setDefaultValues();
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

