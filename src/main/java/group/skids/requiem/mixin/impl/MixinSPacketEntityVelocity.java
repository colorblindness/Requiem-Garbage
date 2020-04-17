package group.skids.requiem.mixin.impl;

import group.skids.requiem.mixin.accessors.ISPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SPacketEntityVelocity.class)
public abstract class MixinSPacketEntityVelocity implements ISPacketEntityVelocity {

    @Override
    @Accessor
    public abstract void setMotionY(int motionY);

    @Override
    @Accessor
    public abstract void setMotionX(int motionX);

    @Override
    @Accessor
    public abstract void setMotionZ(int motionZ);
}
