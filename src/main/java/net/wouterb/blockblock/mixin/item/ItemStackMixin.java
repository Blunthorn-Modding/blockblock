package net.wouterb.blockblock.mixin.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.wouterb.blockblock.util.IPlayerPermissionHelper;
import net.wouterb.blockblock.util.ModLockManager;
import net.wouterb.blockblock.util.ModLockManager.LockType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "getTooltip", at = @At("TAIL"), cancellable = true)
    public void getTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> ci) {
        if (player == null) return;

        List<Text> list = ci.getReturnValue();
        ItemStack stack = (ItemStack) (Object) this;
        String stackId = Registries.ITEM.getId(stack.getItem()).toString();

        IPlayerPermissionHelper playerPermission = (IPlayerPermissionHelper) player;

        if (playerPermission.isBlockLocked(stackId, LockType.BREAKING))
            list.add(1, Text.translatable("tooltip.blockblock.breaking_locked").formatted(Formatting.RED));

        if (playerPermission.isBlockLocked(stackId, LockType.PLACEMENT))
            list.add(1, Text.translatable("tooltip.blockblock.placement_locked").formatted(Formatting.RED));

        if (playerPermission.isBlockLocked(stackId, LockType.BLOCK_INTERACTION))
            list.add(1, Text.translatable("tooltip.blockblock.block_interaction_locked").formatted(Formatting.RED));

        if (playerPermission.isEntityLocked(stackId, LockType.ENTITY_INTERACTION))
            list.add(1, Text.translatable("tooltip.blockblock.entity_interaction_locked").formatted(Formatting.RED));

        if (playerPermission.isEntityLocked(stackId, LockType.ENTITY_DROP))
            list.add(1, Text.translatable("tooltip.blockblock.entity_drop_locked").formatted(Formatting.RED));

        if (playerPermission.isItemLocked(stackId, LockType.ITEM_USAGE))
            list.add(1, Text.translatable("tooltip.blockblock.item_usage_locked").formatted(Formatting.RED));

        if (playerPermission.isItemLocked(stackId, LockType.CRAFTING_RECIPE))
            list.add(1, Text.translatable("tooltip.blockblock.crafting_recipe_locked").formatted(Formatting.RED));
    }

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    public void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> ci) {
        String stackId = Registries.ITEM.getId(context.getStack().getItem()).toString();

        PlayerEntity player = context.getPlayer();

        if (player != null && ((IPlayerPermissionHelper) player).isBlockLocked(stackId, LockType.PLACEMENT))
            ci.setReturnValue(ActionResult.FAIL);
    }
}
