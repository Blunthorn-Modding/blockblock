package net.wouterb.blockblock.mixin.crafting;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.text.Text;
import net.wouterb.blockblock.util.IPlayerPermissionHelper;
import net.wouterb.blockblock.util.ModLockManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;


@Mixin(SmithingScreenHandler.class)
public abstract class SmithingScreenHandlerMixin extends ForgingScreenHandler {

    @Nullable
    @Shadow
    private SmithingRecipe currentRecipe;

    public SmithingScreenHandlerMixin(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "updateResult", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;get(I)Ljava/lang/Object;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void updateResultMixin(CallbackInfo ci, List<SmithingRecipe> list) {
        if (list.isEmpty()) return;

        ItemStack output = list.get(0).getOutput(player.getWorld().getRegistryManager());

        IPlayerPermissionHelper playerPermissionHelper = (IPlayerPermissionHelper) player;
        String itemId = Registries.ITEM.getId(output.getItem()).toString();
        if (playerPermissionHelper.isItemLocked(itemId, ModLockManager.LockType.CRAFTING_RECIPE)) {
            String translationKey = output.getTranslationKey();
            String localizedName = Text.translatable(translationKey).getString();
            ModLockManager.sendLockedFeedbackToPlayer(player, ModLockManager.LockType.CRAFTING_RECIPE, localizedName);
            ci.cancel();
        }
    }
}