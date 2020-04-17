package group.skids.requiem.mixin.impl;

import group.skids.requiem.mixin.accessors.ISPacketExplosion;
import net.minecraft.network.play.server.SPacketExplosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SPacketExplosion.class)
public abstract class MixinSPacketExplosion implements ISPacketExplosion {

    @Override
    @Accessor
    public abstract void setPosY(double posY);

    @Override
    @Accessor
    public abstract void setPosX(double posX);

    @Override
    @Accessor
    public abstract void setPosZ(double posZ);
}
