package net.wouterb.blockblock.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

public class LockedData {
    public static final String LOCKED_DATA_NBT_KEY = "blockblock.locked_data";

    public static void unlockBlock(IEntityDataSaver player, String block_id){
        NbtCompound nbt = player.getPersistentData();
        NbtList lockedBlocks = nbt.getList(LOCKED_DATA_NBT_KEY, NbtCompound.STRING_TYPE);
        lockedBlocks.remove(NbtString.of(block_id));

        nbt.put(LOCKED_DATA_NBT_KEY, lockedBlocks);
    }

    public static void lockBlock(IEntityDataSaver player, String block_id){
        NbtCompound nbt = player.getPersistentData();
        NbtList lockedBlocks = nbt.getList(LOCKED_DATA_NBT_KEY, NbtCompound.STRING_TYPE);
        lockedBlocks.add(NbtString.of(block_id));

        nbt.put(LOCKED_DATA_NBT_KEY, lockedBlocks);
    }

}
