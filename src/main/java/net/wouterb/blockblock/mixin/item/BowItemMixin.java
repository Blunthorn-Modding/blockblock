package net.wouterb.blockblock.mixin.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.wouterb.blockblock.util.mixinhelpers.ItemUsageMixinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BowItem.class)
public class BowItemMixin {
    private ServerPlayerEntity serverPlayerEntity;
    @Inject(method = "use", at = @At("INVOKE"), cancellable = true)
    public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> ci){
        if (ItemUsageMixinHelper.isItemLocked(user, hand)){
            if (user instanceof ServerPlayerEntity playerEntity)
                serverPlayerEntity = playerEntity;
            ci.setReturnValue(TypedActionResult.fail(user.getStackInHand(hand)));
        }
    }

    @Inject(method = "onStoppedUsing", at = @At("INVOKE"), cancellable = true)
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        if (serverPlayerEntity != null && serverPlayerEntity.getUuid() == user.getUuid()) {
            ItemUsageMixinHelper.updateInventory(serverPlayerEntity);
            serverPlayerEntity = null;
        }
    }
}
