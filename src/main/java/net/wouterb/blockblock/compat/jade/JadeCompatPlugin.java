package net.wouterb.blockblock.compat.jade;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadeCompatPlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(BlockComponentProvider.INSTANCE, Block.class);
        registration.registerEntityComponent(EntityComponentProvider.INSTANCE, Entity.class);
    }
}
