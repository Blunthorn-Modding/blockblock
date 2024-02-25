package net.wouterb.blockblock.mixin.entity;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.wouterb.blockblock.util.IPlayerPermissionHelper;
import net.wouterb.blockblock.util.ModLockManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    public void interact(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        EntityType<?> entityType = entity.getType();
        String translationKey = entityType.getTranslationKey();
        String localizedName = Text.translatable(translationKey).getString();
        String entityId = EntityType.getId(entityType).toString();

        if (((IPlayerPermissionHelper) player).isEntityLocked(entityId, ModLockManager.LockType.ENTITY_INTERACTION)){
            ModLockManager.sendLockedFeedbackToPlayer(player, ModLockManager.LockType.ENTITY_INTERACTION, localizedName);
            ci.setReturnValue(ActionResult.FAIL);

            if (player instanceof ServerPlayerEntity playerEntity){
                // Get the player's screen handler (e.g., the inventory screen)
                ScreenHandler screenHandler = playerEntity.currentScreenHandler;

                // Create a DefaultedList containing the updated item stacks
                DefaultedList<ItemStack> updatedStacks = DefaultedList.ofSize(screenHandler.slots.size(), ItemStack.EMPTY);
                for (int i = 0; i < updatedStacks.size(); i++) {
                    updatedStacks.set(i, screenHandler.getSlot(i).getStack());
                }

                // Send the inventory update packet to the client
                InventoryS2CPacket inventoryUpdatePacket = new InventoryS2CPacket(screenHandler.syncId, screenHandler.nextRevision(), updatedStacks, ItemStack.EMPTY);
                playerEntity.networkHandler.sendPacket(inventoryUpdatePacket);
            }
        }
    }
}
