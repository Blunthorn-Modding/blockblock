package net.wouterb.blockblock.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.wouterb.blunthornapi.api.context.BlockActionContext;
import net.wouterb.blunthornapi.api.permission.LockType;
import net.wouterb.blunthornapi.api.permission.Permission;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.wouterb.blockblock.BlockBlock.MOD_ID;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "afterBreak", at = @At("HEAD"), cancellable = true)
    private void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool, CallbackInfo ci) {
        String blockId = Registries.BLOCK.getId(state.getBlock()).toString();
        BlockActionContext blockActionContext = new BlockActionContext(world, player, pos, blockId, LockType.BREAKING);

        if (Permission.isObjectLocked(blockActionContext, MOD_ID))
            ci.cancel();
    }
}
