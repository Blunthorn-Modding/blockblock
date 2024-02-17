package net.wouterb.blockblock.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.wouterb.blockblock.util.IEntityDataSaver;
import net.wouterb.blockblock.util.IPlayerPermissionHelper;
import net.wouterb.blockblock.util.LockedData;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public class ModPlayerPermissionMixin implements IPlayerPermissionHelper {
    @Override
    public boolean isBlockLocked(String blockId) {
        NbtCompound nbt = ((IEntityDataSaver) this).getPersistentData();
        NbtList nbtList = nbt.getList(LockedData.LOCKED_DATA_NBT_KEY, NbtElement.STRING_TYPE);
        return nbtList.contains(NbtString.of(blockId));
    }
}
