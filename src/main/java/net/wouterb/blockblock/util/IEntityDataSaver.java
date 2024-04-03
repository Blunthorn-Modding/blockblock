package net.wouterb.blockblock.util;

import net.minecraft.nbt.NbtCompound;

public interface IEntityDataSaver {
    NbtCompound getPersistentData();
    void setPersistentData(NbtCompound data);

    void resetPersistentData(boolean wipe);

    void setDefaultValues();

    void setEmptyValues();
}
