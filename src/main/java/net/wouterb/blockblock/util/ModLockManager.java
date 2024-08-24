package net.wouterb.blockblock.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.wouterb.blockblock.config.ModConfig;

public class ModLockManager {

    public static enum LockType {
        BREAKING,
        PLACEMENT,
        BLOCK_INTERACTION,
        ENTITY_INTERACTION,
        ENTITY_DROP,
        ITEM_USAGE,
        CRAFTING_RECIPE;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public static void sendLockedFeedbackToPlayer(PlayerEntity player, LockType lockType, String objectId) {
        if (!ModConfig.displayMessagesToUser()) return;

        String message = ModConfig.getMessage(lockType, objectId);
        player.sendMessage(Text.of(message), true);
    }

    public static void unlock(IEntityDataSaver player, String id, LockType lockType, ServerCommandSource source) {
        NbtCompound nbt = player.getPersistentData();
        String nbtKey = getNbtKey(lockType);
        NbtList locked = nbt.getList(nbtKey, NbtCompound.STRING_TYPE);

        if (locked.contains(NbtString.of(id))) {
            locked.remove(NbtString.of(id));
            nbt.put(nbtKey, locked);
            source.sendFeedback(() -> Text.literal("Unlocking " + id + " for " + ((ServerPlayerEntity) player).getName().getString() + " in " + lockType.toString()), false);

        } else {
            source.sendFeedback(() -> Text.literal(((ServerPlayerEntity) player).getName().getString() + " already has " + id + " unlocked in " + lockType.toString()), false);
        }
    }

    public static void lock(IEntityDataSaver player, String id, LockType lockType, ServerCommandSource source){
        NbtCompound nbt = player.getPersistentData();
        String nbtKey = getNbtKey(lockType);
        NbtList locked = nbt.getList(nbtKey, NbtCompound.STRING_TYPE);
        if (!locked.contains(NbtString.of(id))){
            locked.add(NbtString.of(id));
            nbt.put(nbtKey, locked);
            source.sendFeedback(() -> Text.literal("Locking " + id + " for " + ((ServerPlayerEntity)player).getName().getString() + " in " + lockType.toString()), false);
//            ClientLockSyncHandler.updateClient((ServerPlayerEntity) player, nbt);
        } else {
            source.sendFeedback(() -> Text.literal(((ServerPlayerEntity)player).getName().getString() + " already has " + id + " locked in " + lockType.toString()), false);
        }
    }

    public static String getNbtKey(LockType lockType) {
        return lockType.toString();
    }

}
