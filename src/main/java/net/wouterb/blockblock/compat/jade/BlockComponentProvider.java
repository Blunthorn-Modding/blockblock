package net.wouterb.blockblock.compat.jade;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.wouterb.blockblock.util.IPlayerPermissionHelper;
import net.wouterb.blockblock.util.ModLockManager;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;


public enum BlockComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(
            ITooltip tooltip,
            BlockAccessor accessor,
            IPluginConfig config
    ) {
        PlayerEntity player = accessor.getPlayer();

        IPlayerPermissionHelper playerPermission = (IPlayerPermissionHelper) player;

        BlockState state = accessor.getBlockState();
        String blockId = Registries.BLOCK.getId(state.getBlock()).toString();

        if (playerPermission.isBlockLocked(blockId, ModLockManager.LockType.BREAKING))
            tooltip.add(1, Text.translatable("tooltip.blockblock.breaking_locked").formatted(Formatting.RED));

        if (playerPermission.isBlockLocked(blockId, ModLockManager.LockType.BLOCK_INTERACTION))
            tooltip.add(1, Text.translatable("tooltip.blockblock.block_interaction_locked").formatted(Formatting.RED));

        if (playerPermission.isBlockLocked(blockId, ModLockManager.LockType.CRAFTING_RECIPE))
            tooltip.add(1, Text.translatable("tooltip.blockblock.crafting_recipe_locked").formatted(Formatting.RED));
    }

    @Override
    public Identifier getUid() {
        return new Identifier("blockblock", "block_component_provider");

    }
}
