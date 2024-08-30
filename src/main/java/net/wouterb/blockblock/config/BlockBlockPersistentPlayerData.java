package net.wouterb.blockblock.config;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.wouterb.blunthornapi.api.data.IPersistentPlayerData;
import net.wouterb.blunthornapi.api.permission.LockType;

import static net.wouterb.blockblock.BlockBlock.MOD_ID;

public class BlockBlockPersistentPlayerData implements IPersistentPlayerData {
    @Override
    public NbtCompound getDefaultValues() {
        LockedDefaultValues lockedDefaultValues = ModConfigManager.getDefaultLockedValues();
        NbtCompound nbt = new NbtCompound();
        for (LockType lockType : LockType.values()) {
            String[] locked = lockedDefaultValues.getFieldByString(lockType.toString());
            NbtList nbtList = new NbtList();
            for (String id : locked)
                nbtList.add(NbtString.of(id));
            nbt.put(lockType.toString(), nbtList);
        }

        return nbt;
    }

    @Override
    public String getTargetModId() {
        return MOD_ID;
    }
}
