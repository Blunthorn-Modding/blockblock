package net.wouterb.blockblock.compat.jade;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.wouterb.blockblock.BlockBlock;
import net.wouterb.blunthornapi.api.context.BlockActionContext;
import net.wouterb.blunthornapi.api.context.EntityActionContext;
import net.wouterb.blunthornapi.api.context.ItemActionContext;
import net.wouterb.blunthornapi.api.permission.LockType;
import net.wouterb.blunthornapi.api.permission.Permission;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum EntityComponentProvider implements IEntityComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(
            ITooltip tooltip,
            EntityAccessor accessor,
            IPluginConfig config
    ) {
        PlayerEntity player = accessor.getPlayer();

        EntityType<?> entity = accessor.getEntity().getType();
        String entityId = EntityType.getId(entity).toString();
//
        if (entity.equals(EntityType.ITEM)) {
            ItemStack stack = ((ItemEntity)accessor.getEntity()).getStack();
            entityId = Registries.ITEM.getId(stack.getItem()).toString();

            ItemActionContext itemUsageContext = new ItemActionContext(player.getWorld(), player, ((ItemEntity) accessor.getEntity()).getStack(), LockType.ITEM_USAGE);
            if (Permission.isObjectLocked(itemUsageContext, BlockBlock.MOD_ID))
                tooltip.add(1, Text.translatable("tooltip.blockblock.item_usage_locked").formatted(Formatting.RED));
        }

        BlockPos newBlockPos = new BlockPos(0, 0, 0);
        BlockActionContext breakingContext = new BlockActionContext(player.getWorld(), player, newBlockPos, entityId, LockType.BREAKING);
        BlockActionContext placementContext = new BlockActionContext(player.getWorld(), player, newBlockPos, entityId, LockType.PLACEMENT);
        BlockActionContext blockInteractionContext = new BlockActionContext(player.getWorld(), player, newBlockPos, entityId, LockType.BLOCK_INTERACTION);
        BlockActionContext craftingContext = new BlockActionContext(player.getWorld(), player, newBlockPos, entityId, LockType.CRAFTING_RECIPE);
        EntityActionContext entityInteractionContext = new EntityActionContext(player.getWorld(), player, accessor.getEntity(), LockType.ENTITY_INTERACTION);
        EntityActionContext entityDropContext = new EntityActionContext(player.getWorld(), player, accessor.getEntity(), LockType.ENTITY_DROP);


        if (Permission.isObjectLocked(breakingContext, BlockBlock.MOD_ID))
            tooltip.add(1, Text.translatable("tooltip.blockblock.breaking_locked").formatted(Formatting.RED));

        if (Permission.isObjectLocked(placementContext, BlockBlock.MOD_ID))
            tooltip.add(1, Text.translatable("tooltip.blockblock.placement_locked").formatted(Formatting.RED));

        if (Permission.isObjectLocked(blockInteractionContext, BlockBlock.MOD_ID))
            tooltip.add(1, Text.translatable("tooltip.blockblock.block_interaction_locked").formatted(Formatting.RED));

        if (Permission.isObjectLocked(entityInteractionContext, BlockBlock.MOD_ID))
            tooltip.add(1, Text.translatable("tooltip.blockblock.entity_interaction_locked").formatted(Formatting.RED));

        if (Permission.isObjectLocked(entityDropContext, BlockBlock.MOD_ID))
            tooltip.add(1, Text.translatable("tooltip.blockblock.entity_drop_locked").formatted(Formatting.RED));

        if (Permission.isObjectLocked(craftingContext, BlockBlock.MOD_ID))
            tooltip.add(1, Text.translatable("tooltip.blockblock.crafting_recipe_locked").formatted(Formatting.RED));
    }

    @Override
    public Identifier getUid() {
        return new Identifier("blockblock", "entity_component_provider");
    }
}
