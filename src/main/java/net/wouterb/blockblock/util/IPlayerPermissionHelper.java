package net.wouterb.blockblock.util;

import net.minecraft.server.network.ServerPlayerEntity;

public interface IPlayerPermissionHelper {
    boolean isBlockLocked(String blockId, ModLockManager.LockType lockType);

    boolean isEntityLocked(String entityId, ModLockManager.LockType lockType);

    boolean isItemLocked(String itemId, ModLockManager.LockType lockType);

    String getIdOfStructure(ServerPlayerEntity player);
}
