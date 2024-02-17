package net.wouterb.blockblock.mixin.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEvents;
import net.wouterb.blockblock.util.IEntityDataSaver;
import net.wouterb.blockblock.util.LockedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Inject(method = "syncWorldEvent", at = @At("HEAD"))
    private void syncWorldEvent(PlayerEntity player, int eventId, BlockPos pos, int data, CallbackInfo ci){
        if (eventId == WorldEvents.BLOCK_BROKEN){
            NbtCompound nbt = ((IEntityDataSaver) player).getPersistentData();
            NbtList nbtList = nbt.getList(LockedData.LOCKED_DATA_NBT_KEY, NbtElement.STRING_TYPE);
            player.sendMessage(Text.of(nbtList.toString()));
        }
    }
}
