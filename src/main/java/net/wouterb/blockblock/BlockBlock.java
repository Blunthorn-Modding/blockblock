package net.wouterb.blockblock;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
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

//TODO: Longer block breaking if it's locked
//TODO: Option for being unable to break the blocks if breaking is locked
//TODO: Reset command

//TODO: Create integration
//TODO: Tom's Simple Storage integration
//TODO: Backpack integration
//TODO: (Possibly?) Trowel mod integration