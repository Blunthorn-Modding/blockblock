package net.wouterb.blockblock.mixin.block;

import net.minecraft.registry.Registries;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.wouterb.blockblock.util.IPlayerPermissionHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "afterBreak", at = @At("HEAD"), cancellable = true)
    private void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool, CallbackInfo ci) {
//        player.sendMessage(Text.of(Registries.BLOCK.getId(state.getBlock()).toString()));
        String blockId = Registries.BLOCK.getId(state.getBlock()).toString();
        if (((IPlayerPermissionHelper) player).isBlockLocked(blockId))
           ci.cancel();
    }
}

