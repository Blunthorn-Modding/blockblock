package net.wouterb.blockblock.mixin.misc;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.wouterb.blockblock.util.mixinhelpers.ItemUsageMixinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {
    @Shadow
    private ItemStack cursorStack;

    @Inject(method = "internalOnSlotClick", at = @At("HEAD"), cancellable = true)
    public void internalOnSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        ScreenHandler screenHandler = (ScreenHandler)(Object) this;
        if (slotIndex > 0){
            ItemStack item = screenHandler.getSlot(slotIndex).getStack();
            if (8 - MobEntity.getPreferredEquipmentSlot(cursorStack).getEntitySlotId() == slotIndex) {
                if (ItemUsageMixinHelper.isItemLocked(player, cursorStack))
                    ci.cancel();
            } else if (actionType == SlotActionType.QUICK_MOVE && MobEntity.getPreferredEquipmentSlot(item).isArmorSlot()) {
                if (ItemUsageMixinHelper.isItemLocked(player, item))
                    ci.cancel();
            }
        }
    }
}
