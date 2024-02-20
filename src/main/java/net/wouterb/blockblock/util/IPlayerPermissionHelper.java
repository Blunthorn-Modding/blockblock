package net.wouterb.blockblock.util;

public interface IPlayerPermissionHelper {
    boolean isBlockLocked(String blockId, ModLockManager.LOCK_TYPES lockType);
}
