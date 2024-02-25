package net.wouterb.blockblock.mixin.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.wouterb.blockblock.util.IPlayerPermissionHelper;
import net.wouterb.blockblock.util.ModLockManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {
    @Inject(method = "onBlockBreakStart", at = @At("HEAD"))
    private void onBlockBreakStart(World world, BlockPos pos, PlayerEntity player, CallbackInfo ci) {
        BlockState state = world.getBlockState(pos);
        String blockId = Registries.BLOCK.getId(state.getBlock()).toString();

        if (((IPlayerPermissionHelper) player).isBlockLocked(blockId, ModLockManager.LockType.BREAKING)){
            String translationKey = state.getBlock().getTranslationKey();
            String localizedName = Text.translatable(translationKey).getString();
            player.sendMessage(Text.of(String.format("You do not have %s unlocked!", localizedName)), true);
        }
    }

    @Inject(method = "onUse", at = @At("INVOKE"), cancellable = true)
    public void onUse(World world, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> ci) {
        BlockState state = world.getBlockState(hit.getBlockPos());
        String blockId = Registries.BLOCK.getId(state.getBlock()).toString();

        String translationKey = state.getBlock().getTranslationKey();
        String localizedName = Text.translatable(translationKey).getString();
        if (((IPlayerPermissionHelper) player).isBlockLocked(blockId, ModLockManager.LockType.BLOCK_INTERACTION)) {
            player.sendMessage(Text.of(String.format("You do not have %s unlocked!", localizedName)), true);
            ci.setReturnValue(ActionResult.FAIL);
        }

    }
}
