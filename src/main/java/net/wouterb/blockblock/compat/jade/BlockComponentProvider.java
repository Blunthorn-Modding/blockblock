package net.wouterb.blockblock.compat.jade;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.wouterb.blunthornapi.api.context.BlockActionContext;
import net.wouterb.blunthornapi.api.permission.LockType;
import net.wouterb.blunthornapi.api.permission.Permission;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import static net.wouterb.blockblock.BlockBlock.MOD_ID;


public enum BlockComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(
            ITooltip tooltip,
            BlockAccessor accessor,
            IPluginConfig config
    ) {
        PlayerEntity player = accessor.getPlayer();

        BlockState state = accessor.getBlockState();
        String blockId = Registries.BLOCK.getId(state.getBlock()).toString();

        BlockActionContext breakingContext = new BlockActionContext(player.getWorld(), player, accessor.getPosition(), blockId, LockType.BREAKING);
        BlockActionContext placementContext = new BlockActionContext(player.getWorld(), player, accessor.getPosition(), blockId, LockType.PLACEMENT);
        BlockActionContext interactionContext = new BlockActionContext(player.getWorld(), player, accessor.getPosition(), blockId, LockType.BLOCK_INTERACTION);
        BlockActionContext craftingContext = new BlockActionContext(player.getWorld(), player, accessor.getPosition(), blockId, LockType.CRAFTING_RECIPE);

        if (Permission.isObjectLocked(breakingContext, MOD_ID))
            tooltip.add(1, Text.translatable("tooltip.blockblock.breaking_locked").formatted(Formatting.RED));

        if (Permission.isObjectLocked(placementContext, MOD_ID))
            tooltip.add(1, Text.translatable("tooltip.blockblock.placement_locked").formatted(Formatting.RED));

        if (Permission.isObjectLocked(interactionContext, MOD_ID))
            tooltip.add(1, Text.translatable("tooltip.blockblock.block_interaction_locked").formatted(Formatting.RED));

        if (Permission.isObjectLocked(craftingContext, MOD_ID))
            tooltip.add(1, Text.translatable("tooltip.blockblock.crafting_recipe_locked").formatted(Formatting.RED));
    }

    @Override
    public Identifier getUid() {
        return new Identifier("blockblock", "block_component_provider");

    }
}
