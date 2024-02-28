package net.wouterb.blockblock;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.wouterb.blockblock.config.LockedDefaultValues;
import net.wouterb.blockblock.config.ModConfigManager;
import net.wouterb.blockblock.util.IEntityDataSaver;
import net.wouterb.blockblock.util.ModLockManager;
import net.wouterb.blockblock.util.ModRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockBlock implements ModInitializer {
	public static final String MOD_ID = "blockblock";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Starting BlockBlock!");

		ModConfigManager.registerConfig();

		ServerPlayConnectionEvents.JOIN.register(BlockBlock::onPlayerJoin);

		ModRegistries.registerCommands();
	}

	private static void onPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server){
		ServerPlayerEntity player = handler.getPlayer();
		NbtCompound data = ((IEntityDataSaver) player).getPersistentData();

		if (!data.contains(MOD_ID)){
			LOGGER.info("Player without BlockBlock data joined, assigning default values...");
			LockedDefaultValues defaultValues = ModConfigManager.getDefaultLockedValues();
			for (ModLockManager.LockType lockType : ModLockManager.LockType.values()){
				String[] locked = defaultValues.getFieldByString(lockType.toString());
				NbtList nbtList = new NbtList();
				for (String id : locked)
					nbtList.add(NbtString.of(id));

				data.put(lockType.toString(), nbtList);
			}
		}
	}
}