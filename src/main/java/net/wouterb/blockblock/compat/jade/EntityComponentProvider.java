package net.wouterb.blockblock.compat.jade;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.wouterb.blockblock.util.IPlayerPermissionHelper;
import net.wouterb.blockblock.util.ModLockManager;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import static net.wouterb.blockblock.util.mixinhelpers.CraftingRecipeMixinHelper.isRecipeLocked;

public enum EntityComponentProvider implements IEntityComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(
            ITooltip tooltip,
            EntityAccessor accessor,
            IPluginConfig config
    ) {
        PlayerEntity player = accessor.getPlayer();
        IPlayerPermissionHelper playerPermission = (IPlayerPermissionHelper) player;

        EntityType<?> entity = accessor.getEntity().getType();
        String entityId = EntityType.getId(entity).toString();

        if (entity.equals(EntityType.ITEM)) {
            ItemStack stack = ((ItemEntity)accessor.getEntity()).getStack();
            entityId = Registries.ITEM.getId(stack.getItem()).toString();
        }

        if (playerPermission.isBlockLocked(entityId, ModLockManager.LockType.BREAKING))
            tooltip.add(1, Text.translatable("tooltip.blockblock.breaking_locked").formatted(Formatting.RED));

        if (playerPermission.isBlockLocked(entityId, ModLockManager.LockType.PLACEMENT))
            tooltip.add(1, Text.translatable("tooltip.blockblock.placement_locked").formatted(Formatting.RED));

        if (playerPermission.isBlockLocked(entityId, ModLockManager.LockType.BLOCK_INTERACTION))
            tooltip.add(1, Text.translatable("tooltip.blockblock.block_interaction_locked").formatted(Formatting.RED));

        if (playerPermission.isEntityLocked(entityId, ModLockManager.LockType.ENTITY_INTERACTION))
            tooltip.add(1, Text.translatable("tooltip.blockblock.entity_interaction_locked").formatted(Formatting.RED));

        if (playerPermission.isEntityLocked(entityId, ModLockManager.LockType.ENTITY_DROP))
            tooltip.add(1, Text.translatable("tooltip.blockblock.entity_drop_locked").formatted(Formatting.RED));

        if (playerPermission.isItemLocked(entityId, ModLockManager.LockType.ITEM_USAGE))
            tooltip.add(1, Text.translatable("tooltip.blockblock.item_usage_locked").formatted(Formatting.RED));

        if (isRecipeLocked(playerPermission, entityId))
            tooltip.add(1, Text.translatable("tooltip.blockblock.crafting_recipe_locked").formatted(Formatting.RED));
    }

    @Override
    public Identifier getUid() {
        return new Identifier("blockblock", "entity_component_provider");
    }
}
