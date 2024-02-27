package net.wouterb.blockblock.mixin.block;

import net.minecraft.registry.Registries;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.wouterb.blockblock.util.IPlayerPermissionHelper;
import net.wouterb.blockblock.util.ModLockManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


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
}

