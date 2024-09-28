package net.wouterb.blockblock;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.wouterb.blockblock.config.ModConfigManager;

public class BlockBlockClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ModConfigManager.registerConfig();
        });
    }
}
