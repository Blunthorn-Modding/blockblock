package net.wouterb.blockblock;

import net.fabricmc.api.ClientModInitializer;
import net.wouterb.blockblock.util.ModClientRegistries;

public class BlockBlockClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModClientRegistries.registerClientPackets();
    }
}
