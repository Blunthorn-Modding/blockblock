package net.wouterb.blockblock.util;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.wouterb.blockblock.config.ModConfig;
import net.wouterb.blockblock.network.ClientLockSyncHandler;
import net.wouterb.blockblock.network.ConfigSyncHandler;

public class ModClientRegistries {
    public static void registerClientPackets() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> ClientLockSyncHandler.onUpdateReceived(client.player, null));

        ClientPlayNetworking.registerGlobalReceiver(ClientLockSyncHandler.LOCK_LIST_PACKET_ID, ((client, handler, buf, responseSender) -> {
            ClientLockSyncHandler.onUpdateReceived(client.player, buf.readNbt());
        }));

        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncHandler.CONFIG_SYNC_PACKET_ID, ((client, handler, buf, responseSender) -> {
            String fieldName = buf.readString();
            byte type = buf.readByte();

            Object value = switch (type) {
                case ConfigSyncHandler.DataType.BOOL -> buf.readBoolean();
                case ConfigSyncHandler.DataType.INT -> buf.readInt();
                case ConfigSyncHandler.DataType.FLOAT -> buf.readFloat();
                case ConfigSyncHandler.DataType.STRING -> buf.readString();
                default -> null;
            };

            ModConfig.setFieldValue(fieldName, value);
        }));
    }
}
