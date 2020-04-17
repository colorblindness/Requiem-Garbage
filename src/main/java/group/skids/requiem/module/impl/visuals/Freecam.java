package group.skids.requiem.module.impl.visuals;

import group.skids.requiem.events.*;
import group.skids.requiem.module.Module;
import group.skids.requiem.utils.Printer;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.*;

import java.util.Objects;

public class Freecam extends Module {
    private double x,y,z,yaw,pitch;
    public Freecam() {
        super("Freecam", Category.VISUALS, 0xff600666);
    }

    @Override
    public void onEnable() {
        if (getMc().world == null || getMc().player == null) return;
        if (Objects.nonNull(getMc().world)) {
            this.x = getMc().player.posX;
            this.y = getMc().player.posY;
            this.z = getMc().player.posZ;
            this.yaw = getMc().player.rotationYaw;
            this.pitch = getMc().player.rotationPitch;
            final EntityOtherPlayerMP entityOtherPlayerMP = new EntityOtherPlayerMP(getMc().world, getMc().player.getGameProfile());
            entityOtherPlayerMP.inventory = getMc().player.inventory;
            entityOtherPlayerMP.inventoryContainer = getMc().player.inventoryContainer;
            entityOtherPlayerMP.setPositionAndRotation(this.x, getMc().player.getEntityBoundingBox().minY, this.z, getMc().player.rotationYaw, getMc().player.rotationPitch);
            entityOtherPlayerMP.rotationYawHead = getMc().player.rotationYawHead;
            entityOtherPlayerMP.setSneaking(getMc().player.isSneaking());
            getMc().world.addEntityToWorld(-1488, entityOtherPlayerMP);
        }
    }

    @Override
    public void onDisable() {
        if (getMc().world == null || getMc().player == null) return;
        if (Objects.nonNull(getMc().world)) {
            getMc().player.jumpMovementFactor = 0.02f;
            getMc().player.setPosition(this.x, this.y, this.z);
            getMc().player.connection.sendPacket(new CPacketPlayer.Position(getMc().player.posX, getMc().player.posY + 0.01, getMc().player.posZ, getMc().player.onGround));
            getMc().player.noClip = false;
            getMc().world.removeEntityFromWorld(-1488);
            getMc().player.motionY = 0.0;
            getMc().player.rotationPitch = (float) pitch;
            getMc().player.rotationYaw = (float) yaw;
            yaw = pitch = 0;
        }
        getMc().renderGlobal.loadRenderers();
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (getMc().world == null || getMc().player == null) return;
        if (event.getEventType() == EventType.PRE) {
            getMc().player.setVelocity(0.0, 0.0, 0.0);
            getMc().player.jumpMovementFactor = 1;
            if (getMc().currentScreen == null) {
                if (GameSettings.isKeyDown(getMc().gameSettings.keyBindJump)) {
                    getMc().player.motionY += 1;
                }
                if (GameSettings.isKeyDown(getMc().gameSettings.keyBindSneak)) {
                    getMc().player.motionY -= 1;
                }
            }
            getMc().player.noClip = true;
            getMc().player.renderArmPitch = 5000.0f;
        }
    }

    @Subscribe
    public void onMotion(MotionEvent event) {
        if (getMc().world == null || getMc().player == null) return;
        setMoveSpeed(event, 1);
        if (!GameSettings.isKeyDown(getMc().gameSettings.keyBindSneak) && !GameSettings.isKeyDown(getMc().gameSettings.keyBindJump)) {
            event.setY(2.0 * -(getMc().player.rotationPitch / 180.0f) * getMc().player.movementInput.moveForward);
        }
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (getMc().world == null || getMc().player == null) return;
        if (event.getType() == EventType.PRE) {
            if (event.getPacket() instanceof CPacketPlayer) event.setCancelled(true);
            if (event.getPacket() instanceof CPacketPlayerDigging || event.getPacket() instanceof CPacketEntityAction || event.getPacket() instanceof CPacketUseEntity || event.getPacket() instanceof CPacketAnimation)
                event.setCancelled(true);
        }
    }

    @Subscribe
    public void onBB(BoundingBoxEvent event) {
        if (getMc().world == null || getMc().player == null) return;
        if (event.getEntity() == getMc().player) event.setAabb(null);
    }

    @Subscribe
    public void onPush(PushEvent event) {
        if (getMc().world == null || getMc().player == null) return;
        event.setCancelled(true);
    }

    private void setMoveSpeed(MotionEvent event, double speed) {
        double forward = getMc().player.movementInput.moveForward;
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
}
