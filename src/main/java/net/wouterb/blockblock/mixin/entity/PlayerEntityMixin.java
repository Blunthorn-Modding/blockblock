package net.wouterb.blockblock.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.wouterb.blockblock.util.IPlayerPermissionHelper;
import net.wouterb.blockblock.util.mixinhelpers.ItemUsageMixinHelper;
import net.wouterb.blockblock.util.ModLockManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.Console;

@SuppressWarnings("UnreachableCode")
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    public void interact(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        EntityType<?> entityType = entity.getType();
        String translationKey = entityType.getTranslationKey();
        String localizedName = Text.translatable(translationKey).getString();
        String entityId = EntityType.getId(entityType).toString();

        // Entity interaction lock
        if (((IPlayerPermissionHelper) player).isEntityLocked(entityId, ModLockManager.LockType.ENTITY_INTERACTION)){
            ModLockManager.sendLockedFeedbackToPlayer(player, ModLockManager.LockType.ENTITY_INTERACTION, localizedName);
            ci.setReturnValue(ActionResult.FAIL);

            if (player instanceof ServerPlayerEntity playerEntity){
                ItemUsageMixinHelper.updateInventory(playerEntity);
            }
        }

        // Item usage lock (when using an item on an entity)
        if (ItemUsageMixinHelper.isItemLocked(player, hand)){
            ci.setReturnValue(ActionResult.FAIL);
            if (player instanceof ServerPlayerEntity serverPlayer)
                ItemUsageMixinHelper.updateInventory(serverPlayer);
        }
    }

//    @Inject(method = "canEquip", at = @At("HEAD"), cancellable = true)
//    public void canEquip(ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
//        PlayerEntity player = (PlayerEntity) (Object) this;
//        System.out.println("Can equip!");
//        if (ItemUsageMixinHelper.isItemLocked(player, stack)) {
////            player.dropStack(stack);
//            ci.setReturnValue(false);
//        }
//    }
//    @Inject(method = "equipStack", at = @At("HEAD"), cancellable = true)
//    public void equipStack(EquipmentSlot slot, ItemStack stack, CallbackInfo ci) {
//        System.out.println("Equip stack!");
//        if (slot.isArmorSlot()) {
//            PlayerEntity player = (PlayerEntity) (Object) this;
//
//            if (ItemUsageMixinHelper.isItemLocked(player, stack)) {
//                player.dropStack(stack);
//                ci.cancel();
//            }
//        }
//    }

    // Item usage lock (attack specific - DUH!)
    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    public void attack(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        ItemStack tool = player.getStackInHand(Hand.MAIN_HAND);
        String itemId = Registries.ITEM.getId(tool.getItem()).toString();
        IPlayerPermissionHelper playerPermission = (IPlayerPermissionHelper) player;
        if (playerPermission.isItemLocked(itemId, ModLockManager.LockType.ITEM_USAGE)) {
            String translationKey = tool.getTranslationKey();
            String localizedName = Text.translatable(translationKey).getString();
            ModLockManager.sendLockedFeedbackToPlayer(player, ModLockManager.LockType.ITEM_USAGE, localizedName);
            ci.cancel();
        }
    }
}
