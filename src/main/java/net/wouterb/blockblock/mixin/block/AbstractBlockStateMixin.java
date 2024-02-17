package net.wouterb.blockblock.mixin.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.wouterb.blockblock.BlockBlock;
import net.wouterb.blockblock.util.IPlayerPermissionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {
    @Inject(method = "onBlockBreakStart", at = @At("HEAD"))
    private void onBlockBreakStart(World world, BlockPos pos, PlayerEntity player, CallbackInfo ci) {
        BlockState state = world.getBlockState(pos);
        String blockId = Registries.BLOCK.getId(state.getBlock()).toString();

        if (((IPlayerPermissionHelper) player).isBlockLocked(blockId)){
            player.sendMessage(Text.of(String.format("You do not have %s unlocked!", blockId)), true);
        }
    }
}
