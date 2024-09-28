package net.wouterb.blockblock.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.wouterb.blunthornapi.api.context.ActionContext;

public class BlockBlockUtil {

    public static void sendLockedFeedbackToPlayer(ActionContext context) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ModRegistries.sendLockedFeedbackToPlayer(context);
        }
    }
}
