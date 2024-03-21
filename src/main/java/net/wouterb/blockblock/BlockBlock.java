package net.wouterb.blockblock;

import net.fabricmc.api.ModInitializer;
import net.wouterb.blockblock.util.ModRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockBlock implements ModInitializer {
	public static final String MOD_ID = "blockblock";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Starting BlockBlock!");

		ModRegistries.registerConfigs();
		ModRegistries.registerCommands();
		ModRegistries.registerEvents();
	}
}

// General
//TODO: Add help command
//TODO: Move messages to lang file too

// Bugs
//TODO: On death client config resets

// Integration
//TODO: Create mod integration
//TODO: Tom's Simple Storage integration
//TODO: Backpack integration
//TODO: Trowel mod integration
