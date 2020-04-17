package group.skids.requiem.module.impl.movement;

import group.skids.requiem.events.MotionEvent;
import group.skids.requiem.events.UpdateEvent;
import group.skids.requiem.module.Module;
import net.b0at.api.event.Subscribe;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Objects;

public class Speed extends Module {
    private int stage = 1;
    private double moveSpeed, lastDist;
    public Speed() {
        super("Speed", Category.MOVEMENT, new Color(0, 255, 0, 255).getRGB());
    }

    @Override
    public void onEnable() {
        if (getMc().player == null) return;
        lastDist = 0;
        moveSpeed = 0;
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (getMc().world == null || getMc().player == null) return;
        lastDist = Math.sqrt(((getMc().player.posX - getMc().player.prevPosX) * (getMc().player.posX - getMc().player.prevPosX)) + ((getMc().player.posZ - getMc().player.prevPosZ) * (getMc().player.posZ - getMc().player.prevPosZ)));
        lastDist = Math.min(lastDist,5);
    }

    @Subscribe
    public void onMotion(MotionEvent event) {
        if (getMc().world == null || getMc().player == null) return;
        if (getMc().player.isInWater() || getMc().player.isInLava()) return;
        switch (stage) {
            case 0:
                ++stage;
                lastDist = 0.0D;
                break;
            case 2:
                double motionY = 0.4025;
                if ((getMc().player.moveForward != 0.0F || getMc().player.moveStrafing != 0.0F) && getMc().player.onGround) {
                    if (getMc().player.isPotionActive(MobEffects.JUMP_BOOST))
                        motionY += ((getMc().player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
                    event.setY(getMc().player.motionY = motionY);
                    moveSpeed *= 2;
                }
                break;
            case 3:
                moveSpeed = lastDist - (0.789 * (lastDist - getBaseMoveSpeed()));
                break;
            default:
                if ((getMc().world.getCollisionBoxes(getMc().player, getMc().player.getEntityBoundingBox().offset(0.0D, getMc().player.motionY, 0.0D)).size() > 0 || getMc().player.isCollidedVertically) && stage > 0) {
                    stage = getMc().player.moveForward == 0.0F && getMc().player.moveStrafing == 0.0F ? 0 : 1;
                }
                moveSpeed = lastDist - lastDist / 159.0D;
                break;
        }
        moveSpeed = Math.max(moveSpeed, getBaseMoveSpeed());
        setMoveSpeed(event, moveSpeed);
        ++stage;
    }

    private void setMoveSpeed(final MotionEvent event, final double speed) {
        double forward =  getMc().player.movementInput.moveForward;
        double strafe = getMc().player.movementInput.moveStrafe;
        float yaw = getMc().player.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            event.setX(forward * speed * -Math.sin(Math.toRadians(yaw)) + strafe * speed * Math.cos(Math.toRadians(yaw)));
            event.setZ(forward * speed * Math.cos(Math.toRadians(yaw)) - strafe * speed * -Math.sin(Math.toRadians(yaw)));
        }
    }

    private double getBaseMoveSpeed() {
        double baseSpeed = 0.272;
        if (getMc().player.isPotionActive(MobEffects.SPEED)) {
            final int amplifier = Objects.requireNonNull(getMc().player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            baseSpeed *= 1.0 + (0.2 * amplifier);
        }
        return baseSpeed;
    }
}

