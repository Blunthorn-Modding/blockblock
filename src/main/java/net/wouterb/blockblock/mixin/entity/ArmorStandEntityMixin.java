package net.wouterb.blockblock.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.wouterb.blockblock.util.IPlayerPermissionHelper;
import net.wouterb.blockblock.util.ItemUsageMixinHelper;
import net.wouterb.blockblock.util.ModLockManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStandEntity.class)
public class ArmorStandEntityMixin {

    @Inject(method = "interactAt", at = @At("INVOKE"), cancellable = true)
    public void interactAt(PlayerEntity player, Vec3d hitPos, Hand hand, CallbackInfoReturnable<ActionResult> ci) {
        EntityType<?> entityType = ((Entity)(Object)this).getType();
        String translationKey = entityType.getTranslationKey();
        String localizedName = Text.translatable(translationKey).getString();
        String entityId = EntityType.getId(entityType).toString();

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
}
