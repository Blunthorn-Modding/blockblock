package net.wouterb.blockblock.mixin;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.wouterb.blockblock.BlockBlock;
import net.wouterb.blunthornapi.api.context.BlockActionContext;
import net.wouterb.blunthornapi.api.context.EntityActionContext;
import net.wouterb.blunthornapi.api.context.ItemActionContext;
import net.wouterb.blunthornapi.api.permission.LockType;
import net.wouterb.blunthornapi.api.permission.Permission;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "getTooltip", at = @At("TAIL"))
    public void getTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> ci) {
        if (player == null) return;

        List<Text> list = ci.getReturnValue();
        ItemStack stack = (ItemStack) (Object) this;
        String stackId = Registries.ITEM.getId(stack.getItem()).toString();

        BlockPos newBlockPos = new BlockPos(0, 0, 0);
        BlockActionContext breakingContext = new BlockActionContext(player.getWorld(), player, newBlockPos, stackId, net.wouterb.blunthornapi.api.permission.LockType.BREAKING);
        BlockActionContext placementContext = new BlockActionContext(player.getWorld(), player, newBlockPos, stackId, net.wouterb.blunthornapi.api.permission.LockType.PLACEMENT);
        BlockActionContext blockInteractionContext = new BlockActionContext(player.getWorld(), player, newBlockPos, stackId, net.wouterb.blunthornapi.api.permission.LockType.BLOCK_INTERACTION);
        BlockActionContext craftingContext = new BlockActionContext(player.getWorld(), player, newBlockPos, stackId, net.wouterb.blunthornapi.api.permission.LockType.CRAFTING_RECIPE);
        EntityActionContext entityInteractionContext = new EntityActionContext(player.getWorld(), player, stackId, net.wouterb.blunthornapi.api.permission.LockType.ENTITY_INTERACTION);
        EntityActionContext entityDropContext = new EntityActionContext(player.getWorld(), player, stackId, net.wouterb.blunthornapi.api.permission.LockType.ENTITY_DROP);
        ItemActionContext itemUsageContext = new ItemActionContext(player.getWorld(), player, stack, LockType.ITEM_USAGE);

        if (Permission.isObjectLocked(breakingContext, BlockBlock.MOD_ID))
            list.add(1, Text.translatable("tooltip.blockblock.breaking_locked").formatted(Formatting.RED));

        if (Permission.isObjectLocked(placementContext, BlockBlock.MOD_ID))
            list.add(1, Text.translatable("tooltip.blockblock.placement_locked").formatted(Formatting.RED));

        if (Permission.isObjectLocked(blockInteractionContext, BlockBlock.MOD_ID))
            list.add(1, Text.translatable("tooltip.blockblock.block_interaction_locked").formatted(Formatting.RED));

        if (Permission.isObjectLocked(itemUsageContext, BlockBlock.MOD_ID))
            list.add(1, Text.translatable("tooltip.blockblock.item_usage_locked").formatted(Formatting.RED));

        if (Permission.isObjectLocked(entityInteractionContext, BlockBlock.MOD_ID))
            list.add(1, Text.translatable("tooltip.blockblock.entity_interaction_locked").formatted(Formatting.RED));

        if (Permission.isObjectLocked(entityDropContext, BlockBlock.MOD_ID))
            list.add(1, Text.translatable("tooltip.blockblock.entity_drop_locked").formatted(Formatting.RED));

        if (Permission.isObjectLocked(craftingContext, BlockBlock.MOD_ID))
            list.add(1, Text.translatable("tooltip.blockblock.crafting_recipe_locked").formatted(Formatting.RED));


    }
}
