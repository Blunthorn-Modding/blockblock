package net.wouterb.blockblock.mixin.block;

import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraft.world.WorldAccess;
import net.minecraft.world.explosion.Explosion;
import net.wouterb.blockblock.config.ModConfig;
import net.wouterb.blockblock.util.IPlayerPermissionHelper;
import net.wouterb.blockblock.util.ModLockManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "afterBreak", at = @At("HEAD"), cancellable = true)
    private void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool, CallbackInfo ci) {
        String blockId = Registries.BLOCK.getId(state.getBlock()).toString();

        String itemId = Registries.ITEM.getId(tool.getItem()).toString();

        // Breaking with tool lock
        if (((IPlayerPermissionHelper) player).isItemLocked(itemId, ModLockManager.LockType.ITEM_USAGE)) {
            String translationKey = tool.getTranslationKey();
            String localizedName = Text.translatable(translationKey).getString();

            ModLockManager.sendLockedFeedbackToPlayer(player, ModLockManager.LockType.ITEM_USAGE, localizedName);
            ci.cancel();
        }

        // General block breaking lock
        if (((IPlayerPermissionHelper) player).isBlockLocked(blockId, ModLockManager.LockType.BREAKING))
           ci.cancel();
    }

    @Inject(method = "shouldDropItemsOnExplosion", at = @At("TAIL"), cancellable = true)
    public void shouldDropItemsOnExplosion(Explosion explosion, CallbackInfoReturnable<Boolean> ci) {
        LivingEntity entity = explosion.getCausingEntity();
        if (!(entity instanceof PlayerEntity player)) return;

        BlockState state = ((Block) (Object) this).getDefaultState();
        String blockId = Registries.BLOCK.getId(state.getBlock()).toString();
        if (((IPlayerPermissionHelper) player).isBlockLocked(blockId, ModLockManager.LockType.BREAKING))
            ci.setReturnValue(false);
    }
}

