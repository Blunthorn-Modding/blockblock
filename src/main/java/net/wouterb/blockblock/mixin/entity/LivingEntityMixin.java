package net.wouterb.blockblock.mixin.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.wouterb.blockblock.util.IPlayerPermissionHelper;
import net.wouterb.blockblock.util.ItemUsageMixinHelper;
import net.wouterb.blockblock.util.ModLockManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "drop", at=@At("INVOKE"), cancellable = true)
    public void drop(DamageSource source, CallbackInfo ci) {
        Entity sourceAttacker = source.getAttacker();
        EntityType<?> entity = ((LivingEntity) (Object) this).getType();
        String translationKey = entity.getTranslationKey();
        String localizedName = Text.translatable(translationKey).getString();
        String entityId = EntityType.getId(entity).toString();

        if (sourceAttacker instanceof PlayerEntity player){
            if (((IPlayerPermissionHelper) player).isEntityLocked(entityId, ModLockManager.LockType.ENTITY_DROP)){
                ModLockManager.sendLockedFeedbackToPlayer(player, ModLockManager.LockType.ITEM_USAGE, localizedName);
                ci.cancel();
            }
        }
    }

    @Inject(method = "tryUseTotem", at=@At("HEAD"), cancellable = true)
    private void tryUseTotem(DamageSource source, CallbackInfoReturnable<Boolean> ci) {
        if (((Object)this) instanceof PlayerEntity player) {
            if (ItemUsageMixinHelper.isItemLocked(player, Items.TOTEM_OF_UNDYING.getDefaultStack())) {
                ci.setReturnValue(false);
            }
        }
    }
}
