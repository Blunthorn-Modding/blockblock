package net.wouterb.blockblock.mixin.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.wouterb.blockblock.BlockBlock;
import net.wouterb.blockblock.util.IEntityDataSaver;
import net.wouterb.blockblock.util.IPlayerPermissionHelper;
import net.wouterb.blockblock.util.ModLockManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public class ModPlayerPermissionMixin implements IPlayerPermissionHelper {
    @Override
    public boolean isBlockLocked(String blockId, ModLockManager.LockType lockType) {
        return isObjectLocked(blockId, lockType, Registries.BLOCK);
//        NbtList nbtList = getListOfLockedObjects(lockType);
//
//        if (nbtList.contains(NbtString.of(blockId))){
//            return true;
//        }
//
//        Block block = Registries.BLOCK.getOrEmpty(new Identifier(blockId)).orElse(null);
//        if (block == null) {
//            BlockBlock.LOGGER.warn(String.format("Block %s not found!", blockId));
//            return false; // Block not found, consider it not locked
//        }
//        BlockState state = block.getDefaultState();
//
//        for (NbtElement entry : nbtList) {
//            String nbtString = entry.asString();
//            TagKey<Block> tagKey = TagKey.of(Registries.BLOCK.getKey(), new Identifier(nbtString));
//            if (state.isIn(tagKey)) {
//                return true;
//            }
//        }
//        return false;
    }

    @Override
    public boolean isEntityLocked(String entityId, ModLockManager.LockType lockType) {
        return isObjectLocked(entityId, lockType, Registries.ENTITY_TYPE);
//        NbtList nbtList = getListOfLockedObjects(lockType);
//
//        if (nbtList.contains(NbtString.of(entityId))){
//            return true;
//        }
//
//        EntityType<?> entity = Registries.ENTITY_TYPE.getOrEmpty(new Identifier(entityId)).orElse(null);
//        if (entity == null) {
//            BlockBlock.LOGGER.warn(String.format("Entity %s not found!", entityId));
//            return false;
//        }
//
//        for (NbtElement entry : nbtList) {
//            String nbtString = entry.asString();
//            TagKey<EntityType<?>> tagKey = TagKey.of(Registries.ENTITY_TYPE.getKey(), new Identifier(nbtString));
//            if (entity.isIn(tagKey)) {
//                return true;
//            }
//        }
//        return false;
    }

    private boolean isObjectLocked(String objectId, ModLockManager.LockType lockType, Registry<?> registry) {
        NbtList nbtList = getListOfLockedObjects(lockType);

        if (nbtList.contains(NbtString.of(objectId))) {
            return true;
        }

        Object object = registry.getOrEmpty(new Identifier(objectId)).orElse(null);
        if (object == null) {
            BlockBlock.LOGGER.warn(String.format("%s not found!", objectId));
            return false;
        }

        for (NbtElement entry : nbtList) {
            String nbtString = entry.asString();
            TagKey<?> entryTagKey = TagKey.of(registry.getKey(), new Identifier(nbtString));
            if (object instanceof Block && ((Block) object).getDefaultState().isIn((TagKey<Block>) entryTagKey)) {
                return true;
            } else if (object instanceof EntityType && ((EntityType<?>) object).isIn((TagKey<EntityType<?>>) entryTagKey)) {
                return true;
            }
        }

        return false;
    }

    private NbtList getListOfLockedObjects(ModLockManager.LockType lockType){
        NbtCompound nbt = ((IEntityDataSaver) this).getPersistentData();
        return nbt.getList(ModLockManager.getNbtKey(lockType), NbtElement.STRING_TYPE);
    }

}
