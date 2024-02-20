package net.wouterb.blockblock.mixin.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.registry.tag.BlockTags;
import net.wouterb.blockblock.BlockBlock;
import net.wouterb.blockblock.util.IEntityDataSaver;
import net.wouterb.blockblock.util.IPlayerPermissionHelper;
import net.wouterb.blockblock.util.ModLockManager;
import net.fabricmc.fabric.api.tag.convention.v1.TagUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.slf4j.Logger;

@Mixin(Entity.class)
public class ModPlayerPermissionMixin implements IPlayerPermissionHelper {
    public boolean isBlockLocked(String blockId, ModLockManager.LOCK_TYPES lockType) {
        NbtCompound nbt = ((IEntityDataSaver) this).getPersistentData();
        NbtList nbtList = nbt.getList(ModLockManager.getNbtKey(lockType), NbtElement.STRING_TYPE);

        if (nbtList.contains(NbtString.of(blockId))){
            return true;
        }

        Block block = Registries.BLOCK.getOrEmpty(new Identifier(blockId)).orElse(null);
        if (block == null) {
            BlockBlock.LOGGER.info("Block not found!");
            return false; // Block not found, consider it not locked
        }
        BlockState state = block.getDefaultState();

        for (NbtElement entry : nbtList) {
            String nbtString = entry.asString();
            TagKey<Block> tagKey = TagKey.of(Registries.BLOCK.getKey(), new Identifier(nbtString));
            if (state.isIn(tagKey)) {
                return true;
            }
        }
        return false;
    }

}
