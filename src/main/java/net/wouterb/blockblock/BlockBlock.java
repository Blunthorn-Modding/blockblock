package net.wouterb.blockblock;

import net.fabricmc.api.ModInitializer;
import net.wouterb.blockblock.config.BlockBlockPersistentPlayerData;
import net.wouterb.blockblock.util.ModRegistries;
import net.wouterb.blunthornapi.api.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockBlock implements ModInitializer {
	public static final String MOD_ID = "blockblock";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Starting BlockBlock!");

		BlockBlockPersistentPlayerData persistentPlayerData = new BlockBlockPersistentPlayerData();
		Api.registerMod(MOD_ID, persistentPlayerData);

		ModRegistries.registerConfigs();
		ModRegistries.registerCommands();
		ModRegistries.registerEvents();
	}
}
