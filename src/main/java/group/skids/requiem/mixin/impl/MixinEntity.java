package group.skids.requiem.mixin.impl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public class MixinEntity {


    @Shadow
    public float rotationYaw;
    @Shadow
    public float rotationPitch;
    @Shadow
    public double posY;
    @Shadow
    public boolean onGround;

    @Shadow
    public void move(MoverType p_move_1_, double p_move_2_, double p_move_4_, double p_move_6_) {}

    public float getRotationYaw() {
        return rotationYaw;
    }

    public float getRotationPitch() {
        return rotationPitch;
    }

    public double getPosY() {
        return posY;
    }

    public boolean isOnGround() {
        return onGround;
    }
}