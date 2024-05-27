package net.wouterb.blockblock.mixin.entity;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.StructureType;
import net.wouterb.blockblock.config.ModConfig;
import net.wouterb.blockblock.util.IEntityDataSaver;
import net.wouterb.blockblock.util.IPlayerPermissionHelper;
import net.wouterb.blockblock.util.ModLockManager;
import org.spongepowered.asm.mixin.Mixin;
import java.util.Map;

@SuppressWarnings("UnreachableCode")
@Mixin(PlayerEntity.class)

public class PlayerPermissionMixin implements IPlayerPermissionHelper {
    @Override
    public boolean isBlockLocked(String blockId, ModLockManager.LockType lockType) {
        return isObjectLocked(blockId, lockType, Registries.BLOCK);
    }

    @Override
    public boolean isEntityLocked(String entityId, ModLockManager.LockType lockType) {
        return isObjectLocked(entityId, lockType, Registries.ENTITY_TYPE);
    }

    @Override
    public boolean isItemLocked(String itemId, ModLockManager.LockType lockType) {
        return isObjectLocked(itemId, lockType, Registries.ITEM);
    }

    private boolean isObjectLocked(String objectId, ModLockManager.LockType lockType, Registry<?> registry) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.isCreative() && ModConfig.getCreativeBypassesRestrictions()) return false;

        NbtList nbtList = getListOfLockedObjects(lockType);

        if (nbtList.contains(NbtString.of(objectId))) {
            return true;
        }

        if (!player.getWorld().isClient) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
            String currentStructureId = getIdOfStructure(serverPlayerEntity);
            if (currentStructureId != null){
                if (nbtList.contains(NbtString.of(currentStructureId)))
                    return true;
            }
        }

        Object object = registry.getOrEmpty(new Identifier(objectId)).orElse(null);
        if (object == null) return false;

        for (NbtElement entry : nbtList) {
            String nbtString = entry.asString();

            if (!nbtString.startsWith("#")) continue;

            nbtString = nbtString.replace("#", "");

            if (object instanceof Item)
                object = ((Item) object).getDefaultStack();

            TagKey<?> entryTagKey = TagKey.of(registry.getKey(), new Identifier(nbtString));
            if (object instanceof Block && ((Block) object).getDefaultState().isIn((TagKey<Block>) entryTagKey)) {
                return true;
            } else if (object instanceof EntityType && ((EntityType<?>) object).isIn((TagKey<EntityType<?>>) entryTagKey)) {
                return true;
            } else if (object instanceof ItemStack && ((ItemStack) object).isIn((TagKey<Item>) entryTagKey)) {
                return true;
            }
        }



        return false;
    }

    private NbtList getListOfLockedObjects(ModLockManager.LockType lockType){
        NbtCompound nbt = ((IEntityDataSaver) this).getPersistentData();
        return nbt.getList(ModLockManager.getNbtKey(lockType), NbtElement.STRING_TYPE);
    }

    private String getIdOfStructure(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        StructureAccessor structureAccessor = world.getStructureAccessor();
        BlockPos playerPos = player.getBlockPos();
        boolean chunkContainsStructure = structureAccessor.hasStructureReferences(playerPos);
//        System.out.println("chunkContainsStructure: " + chunkContainsStructure);
        if (chunkContainsStructure) {
            // Will run if the player is inside a chunk that has a structure
            Registry<StructureType<?>> structureRegistry = Registries.STRUCTURE_TYPE;

            Map<Structure, LongSet> structureMap = structureAccessor.getStructureReferences(playerPos);
            for (Structure structure : structureMap.keySet()){
                StructureStart structureStart = structureAccessor.getStructureContaining(playerPos, structure);
//                StructureStart structureStart = structureAccessor.getStructureAt(playerPos, structure);
                if (structureStart != StructureStart.DEFAULT) {
                    // Will run if the player is actually inside a structure
                    for (RegistryEntry<StructureType<?>> entry : structureRegistry.getIndexedEntries()) {
//                        System.out.println(entry.getKey().get().getValue().toString());
                        if (entry.value() == structure.getType()) {
                            return entry.getKey().get().getValue().toString();
                        }
                    }
                }
            }
        }
        return null;
    }
}
