package net.wouterb.blockblock;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.wouterb.blockblock.config.JsonConfig;
import net.wouterb.blockblock.util.IEntityDataSaver;
import net.wouterb.blockblock.util.LockedData;
import net.wouterb.blockblock.util.ModRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BlockBlock implements ModInitializer {
	public static final String MOD_ID = "blockblock";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Starting BlockBlock!");
		JsonConfig.registerConfig();

		ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> {
			ServerPlayerEntity player = handler.getPlayer();
			NbtCompound data = ((IEntityDataSaver) player).getPersistentData();
			if (!data.contains(LockedData.LOCKED_DATA_NBT_KEY)){
				player.sendMessage(Text.of("Welcome first timer!"));
				List<String> configData = JsonConfig.getConfigData();
				NbtList nbtList = new NbtList();

				for (String id : configData)
					nbtList.add(NbtString.of(id));

				data.put(LockedData.LOCKED_DATA_NBT_KEY, nbtList);
			}
		}));


		ModRegistries.registerCommands();
	}
}