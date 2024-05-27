package net.wouterb.blockblock.mixin.item;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.wouterb.blockblock.util.mixinhelpers.ItemUsageMixinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {
    @Inject(method = "dispenseArmor", at=@At("INVOKE"), cancellable = true)
    private static void dispenseArmor(BlockPointer pointer, ItemStack armor, CallbackInfoReturnable<Boolean> ci) {
        BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
        List<LivingEntity> list = pointer.world().getEntitiesByClass(LivingEntity.class, new Box(blockPos), EntityPredicates.EXCEPT_SPECTATOR.and(new EntityPredicates.Equipable(armor)));
        if (list.isEmpty()) {
            return;
        }
        LivingEntity livingEntity = (LivingEntity)list.get(0);

        if (livingEntity instanceof PlayerEntity player) {
            if (ItemUsageMixinHelper.isItemLocked(player, armor)){
                ci.setReturnValue(false);
            }
        }
    }
}
