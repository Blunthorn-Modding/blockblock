package net.wouterb.blockblock.util.mixinhelpers;

import net.wouterb.blockblock.util.IPlayerPermissionHelper;
import net.wouterb.blockblock.util.ModLockManager;

public class CraftingRecipeMixinHelper {
    public static boolean isRecipeLocked(IPlayerPermissionHelper player, String objectId) {
        if (player.isItemLocked(objectId, ModLockManager.LockType.CRAFTING_RECIPE))
            return true;
        else if (player.isBlockLocked(objectId, ModLockManager.LockType.CRAFTING_RECIPE))
            return true;
        else return player.isEntityLocked(objectId, ModLockManager.LockType.CRAFTING_RECIPE);
    }
}
