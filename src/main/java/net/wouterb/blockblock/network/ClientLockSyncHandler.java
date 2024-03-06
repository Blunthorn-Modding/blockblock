package net.wouterb.blockblock.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.wouterb.blockblock.util.IEntityDataSaver;
import net.wouterb.blockblock.util.IPlayerPermissionHelper;
import org.jetbrains.annotations.Nullable;

public class ClientLockSyncHandler {
    public static NbtCompound storedPersistentData;
    public static final Identifier LOCK_LIST_PACKET_ID = new Identifier("blockblock", "lock_list_sync");
    public static void updateClient(ServerPlayerEntity player, NbtCompound persistentData) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeNbt(persistentData);
        ServerPlayNetworking.send(player, LOCK_LIST_PACKET_ID, buf);
    }

    public static void onUpdateReceived(@Nullable PlayerEntity player, @Nullable NbtCompound persistentData) {
        if (player == null) {
            storedPersistentData = persistentData;
            return;
        }

        if (persistentData == null)
            persistentData = storedPersistentData;

        IEntityDataSaver dataSaver = (IEntityDataSaver) player;
        dataSaver.setPersistentData(persistentData);
    }
}
