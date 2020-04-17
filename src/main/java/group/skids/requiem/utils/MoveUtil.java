package group.skids.requiem.utils;

import group.skids.requiem.events.MotionEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;

public class MoveUtil {

    private static Minecraft mc = Minecraft.getMinecraft();

    public static void setJumpSpeed(float multiplier) {
        if (mc.player.isSprinting()) {
            float f = mc.player.rotationYaw * 0.017453292F;
            float speed = 0.2F * multiplier;
            mc.player.motionX -= (double)(MathHelper.sin(f) * speed);
            mc.player.motionZ += (double)(MathHelper.cos(f) * speed);
        }
        mc.player.isAirBorne = true;
    }

    public static void setMoveSpeed(final MotionEvent event, final double speed) {
        double forward = mc.player.movementInput.moveForward;
        double strafe = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;
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

    public static void TP(MotionEvent event, double speed, double y) {
        float yaw = mc.player.rotationYaw;
        final float forward = mc.player.moveForward;
        final float strafe = mc.player.moveStrafing;
        yaw += ((forward < 0.0f) ? 180 : 0);
        if (strafe < 0.0f) {
            yaw += ((forward < 0.0f) ? -45 : ((forward == 0.0f) ? 90 : 45));
        }
        if (strafe > 0.0f) {
            yaw -= ((forward < 0.0f) ? -45 : ((forward == 0.0f) ? 90 : 45));
        }
        float direction =  yaw * 0.017453292f;

        final double posX = mc.player.posX;
        final double posY = mc.player.posY;
        final double posZ = mc.player.posZ;
        final double raycastFirstX = -Math.sin(direction);
        final double raycastFirstZ = Math.cos(direction);
        final double raycastFinalX = raycastFirstX * speed;
        final double raycastFinalZ = raycastFirstZ * speed;
        mc.player.connection.sendPacket(new CPacketPlayer.Position(posX + raycastFinalX, posY + y, posZ + raycastFinalZ, mc.player.onGround));
        mc.player.setPosition(posX + raycastFinalX, posY + y, posZ + raycastFinalZ);
    }

    public static float getDirection() {
        float yaw = mc.player.rotationYaw;
        final float forward = mc.player.moveForward;
        final float strafe = mc.player.moveStrafing;
        yaw += ((forward < 0.0f) ? 180 : 0);
        if (strafe < 0.0f) {
            yaw += ((forward < 0.0f) ? -45 : ((forward == 0.0f) ? 90 : 45));
        }
        if (strafe > 0.0f) {
            yaw -= ((forward < 0.0f) ? -45 : ((forward == 0.0f) ? 90 : 45));
        }
        return yaw * 0.017453292f;
    }

    public static double square(final double in) {
        return in * in;
    }

    public static double getSpeed() {
        return Math.hypot(mc.player.motionX, mc.player.motionZ);
    }

    public static void setSpeed(final double speed) {
        mc.player.motionX = -MathHelper.sin(getDirection()) * speed;
        mc.player.motionZ = MathHelper.cos(getDirection()) * speed;
    }

}
