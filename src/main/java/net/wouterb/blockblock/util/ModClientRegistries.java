package net.wouterb.blockblock.util;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.wouterb.blockblock.network.ClientLockSyncHandler;

public class ModClientRegistries {
    public static void registerClientPackets() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> ClientLockSyncHandler.onUpdateReceived(client.player, null));

        ClientPlayNetworking.registerGlobalReceiver(ClientLockSyncHandler.LOCK_LIST_PACKET_ID, ((client, handler, buf, responseSender) -> {
            ClientLockSyncHandler.onUpdateReceived(client.player, buf.readNbt());
        }));
    }
}
