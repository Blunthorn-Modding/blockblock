package net.wouterb.blockblock.util.mixinhelpers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.wouterb.blockblock.util.IPlayerPermissionHelper;
import net.wouterb.blockblock.util.ModLockManager;

public class ItemUsageMixinHelper {
    /**
     * Helper function for all item classes. Mixing into the Item class 'useOnBlock' does nothing, meaning
     * individual class mixin's need to be made. It's all the same code, which is this function.
     * @param context the ItemUsageContext
     * @return whether the player is allowed to use the item.
     */
    public static boolean isItemLocked(ItemUsageContext context){
        PlayerEntity player = context.getPlayer();
        if (player != null)
            return isItemLocked(player, context.getHand());
        return false;
    }

    public static boolean isItemLocked(PlayerEntity player, ItemStack item) {
        String itemId = Registries.ITEM.getId(item.getItem()).toString();
        IPlayerPermissionHelper playerPermission = (IPlayerPermissionHelper) player;
        if (playerPermission.isItemLocked(itemId, ModLockManager.LockType.ITEM_USAGE)) {
            String translationKey = item.getTranslationKey();
            String localizedName = Text.translatable(translationKey).getString();
            ModLockManager.sendLockedFeedbackToPlayer(player, ModLockManager.LockType.ITEM_USAGE, localizedName);
            return true;
        }
        return false;
    }

    public static boolean isItemLocked(PlayerEntity player, Hand hand) {
        ItemStack tool = player.getStackInHand(hand);
        return isItemLocked(player, tool);
    }

    public static void updateInventory(ServerPlayerEntity player) {
        ScreenHandler screenHandler = player.currentScreenHandler;

        DefaultedList<ItemStack> updatedStacks = DefaultedList.ofSize(screenHandler.slots.size(), ItemStack.EMPTY);
        for (int i = 0; i < updatedStacks.size(); i++) {
            updatedStacks.set(i, screenHandler.getSlot(i).getStack());
        }

        InventoryS2CPacket inventoryUpdatePacket = new InventoryS2CPacket(screenHandler.syncId, screenHandler.nextRevision(), updatedStacks, ItemStack.EMPTY);
        player.networkHandler.sendPacket(inventoryUpdatePacket);
    }
}
