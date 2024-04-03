package net.wouterb.blockblock.mixin.item;

import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.wouterb.blockblock.util.mixinhelpers.ItemUsageMixinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CompassItem.class)
public class CompassItemMixin {
    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    public void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> ci){
        if (ItemUsageMixinHelper.isItemLocked(context)) {
            ci.setReturnValue(ActionResult.FAIL);
            if (context.getPlayer() instanceof ServerPlayerEntity player)
                ItemUsageMixinHelper.updateInventory(player);
        }
    }
}
