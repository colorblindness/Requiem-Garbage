package group.skids.requiem.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CombatUtil {

    public static double yawDist(EntityLivingBase e) {
        if (e != null) {
            final Vec3d difference = e.getPositionVector().addVector(0.0, e.getEyeHeight() / 2.0f, 0.0).subtract(Minecraft.getMinecraft().player.getPositionVector().addVector(0.0, Minecraft.getMinecraft().player.getEyeHeight(), 0.0));
            final double d = Math.abs(Minecraft.getMinecraft().player.rotationYaw - (Math.toDegrees(Math.atan2(difference.z, difference.x)) - 90.0f)) % 360.0f;
            return (d > 180.0f) ? (360.0f - d) : d;
        }
        return 0;
    }

    public static float[] getRotationsToEnt(Entity ent, EntityPlayerSP playerSP) {
        final double differenceX = ent.posX - playerSP.posX;
        final double differenceY = (ent.posY + ent.height) - (playerSP.posY + playerSP.height);
        final double differenceZ = ent.posZ - playerSP.posZ;
        final float rotationYaw = (float) (Math.atan2(differenceZ, differenceX) * 180.0D / Math.PI) - 90.0f;
        final float rotationPitch = (float) (Math.atan2(differenceY, playerSP.getDistanceToEntity(ent)) * 180.0D / Math.PI);
        final float finishedYaw = playerSP.rotationYaw + MathHelper.wrapDegrees(rotationYaw - playerSP.rotationYaw);
        final float finishedPitch = playerSP.rotationPitch + MathHelper.wrapDegrees(rotationPitch - playerSP.rotationPitch);
        return new float[]{finishedYaw, -finishedPitch};
    }
}
