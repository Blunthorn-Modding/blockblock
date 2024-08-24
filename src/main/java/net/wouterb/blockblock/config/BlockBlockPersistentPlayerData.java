package net.wouterb.blockblock.config;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.wouterb.blockblock.util.ModLockManager;
import net.wouterb.blunthornapi.api.data.IPersistentPlayerData;

import static net.wouterb.blockblock.BlockBlock.MOD_ID;

public class BlockBlockPersistentPlayerData implements IPersistentPlayerData {
    @Override
    public NbtCompound getDefaultValues() {
        LockedDefaultValues lockedDefaultValues = ModConfigManager.getDefaultLockedValues();
        NbtCompound nbt = new NbtCompound();
        for (ModLockManager.LockType lockType : ModLockManager.LockType.values()) {
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
