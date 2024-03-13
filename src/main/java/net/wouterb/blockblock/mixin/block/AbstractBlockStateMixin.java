package net.wouterb.blockblock.mixin.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.wouterb.blockblock.config.ModConfig;
import net.wouterb.blockblock.util.IPlayerPermissionHelper;
import net.wouterb.blockblock.util.ModLockManager;
import net.wouterb.blockblock.util.ModLockManager.LockType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {
    // Block interaction lock
    @Inject(method = "onUse", at = @At("INVOKE"), cancellable = true)
    public void onUse(World world, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> ci) {
        BlockState state = world.getBlockState(hit.getBlockPos());
        String blockId = Registries.BLOCK.getId(state.getBlock()).toString();

        if (((IPlayerPermissionHelper) player).isBlockLocked(blockId, LockType.BLOCK_INTERACTION)) {
            String translationKey = state.getBlock().getTranslationKey();
            String localizedName = Text.translatable(translationKey).getString();
            ModLockManager.sendLockedFeedbackToPlayer(player, LockType.BLOCK_INTERACTION, localizedName);
            ci.setReturnValue(ActionResult.FAIL);
        }
    }

    @Inject(method = "calcBlockBreakingDelta", at = @At("TAIL"), cancellable = true)
    public void calcBlockBreakingDelta(PlayerEntity player, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> ci) {
        BlockState state = world.getBlockState(pos);
        String blockId = Registries.BLOCK.getId(state.getBlock()).toString();

        ItemStack tool = player.getStackInHand(Hand.MAIN_HAND);
        String itemId = Registries.ITEM.getId(tool.getItem()).toString();

        IPlayerPermissionHelper playerPermissionHelper = (IPlayerPermissionHelper) player;

        boolean isBlockLocked = playerPermissionHelper.isBlockLocked(blockId, LockType.BREAKING);
        boolean isItemLocked = playerPermissionHelper.isItemLocked(itemId, LockType.ITEM_USAGE);

        if (isBlockLocked) {
            String translationKey = state.getBlock().getTranslationKey();
            String localizedName = Text.translatable(translationKey).getString();
            ModLockManager.sendLockedFeedbackToPlayer(player, LockType.BREAKING, localizedName);
        } else if (isItemLocked) {
            String translationKey = tool.getTranslationKey();
            String localizedName = Text.translatable(translationKey).getString();
            ModLockManager.sendLockedFeedbackToPlayer(player, LockType.ITEM_USAGE, localizedName);
        }

        if (isBlockLocked || isItemLocked){
            float originalDelta = ci.getReturnValueF();
            float timeModifier = ModConfig.getLockedBreakTimeModifier();
            float newDelta;
            if (timeModifier == 0f)
                newDelta = 1f;
            else
                newDelta = originalDelta / timeModifier;

            if (ModConfig.getBreakingLockedPreventsBreaking())
                newDelta = 0f;

            ci.setReturnValue(newDelta);
        }
    }
}
