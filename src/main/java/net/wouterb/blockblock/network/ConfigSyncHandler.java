package net.wouterb.blockblock.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.wouterb.blockblock.BlockBlock;
import net.wouterb.blockblock.config.ModConfig;
import net.wouterb.blockblock.util.IEntityDataSaver;
import net.wouterb.blockblock.util.config.StoreInConfig;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class ConfigSyncHandler {
    public static class DataType {
        public static final int BOOL = 0;
        public static final int INT = 1;
        public static final int FLOAT = 2;
        public static final int STRING = 3;

    }
    public static final Identifier CONFIG_SYNC_PACKET_ID = new Identifier("blockblock", "config_sync");

    public static void updateClient(ServerPlayerEntity player) {
        Field[] fields = ModConfig.class.getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if (!field.isAnnotationPresent(StoreInConfig.class)) continue;

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeString(field.getName());

                Object value = field.get(null);

                if (value instanceof String) {
                    buf.writeByte(DataType.STRING);
                    buf.writeString(value.toString());
                }
                else if (value instanceof Float) {
                    buf.writeByte(DataType.FLOAT);
                    buf.writeFloat(Float.parseFloat(value.toString()));
                }
                else if (value instanceof Integer) {
                    buf.writeByte(DataType.INT);
                    buf.writeInt(Integer.parseInt(value.toString()));
                }
                else if (value instanceof Boolean) {
                    buf.writeByte(DataType.BOOL);
                    buf.writeBoolean(Boolean.parseBoolean(value.toString()));
                }

                ServerPlayNetworking.send(player, CONFIG_SYNC_PACKET_ID, buf);
            } catch (IllegalAccessException e) {
                BlockBlock.LOGGER.error(e.toString());
            }
        }
    }
}
