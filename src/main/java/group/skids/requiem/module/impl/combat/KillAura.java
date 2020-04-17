package group.skids.requiem.module.impl.combat;

import group.skids.requiem.client.Requiem;
import group.skids.requiem.events.PacketEvent;
import group.skids.requiem.events.UpdateEvent;
import group.skids.requiem.module.Module;
import group.skids.requiem.utils.CombatUtil;
import group.skids.requiem.utils.TickRate;
import group.skids.requiem.utils.TimerUtil;
import group.skids.requiem.utils.value.impl.BooleanValue;
import group.skids.requiem.utils.value.impl.EnumValue;
import group.skids.requiem.utils.value.impl.NumberValue;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class KillAura extends Module {
    private EntityLivingBase target;
    private final EnumValue<sortmode> sortMode = new EnumValue<>("Sort Mode", sortmode.FOV);
    private final NumberValue<Float> range = new NumberValue<>("Range", 4.2F, 1.0F, 7.0F, 0.1F);
    private final BooleanValue players = new BooleanValue("Players", true);
    private final BooleanValue mobs = new BooleanValue("Mobs", false);
    private final BooleanValue animals = new BooleanValue("Animals", false);
    private final BooleanValue passives = new BooleanValue("Passives", false);
    private final BooleanValue swordcheck = new BooleanValue("SwordCheck", false);
    private final BooleanValue invisibles = new BooleanValue("Invisibles", true, players, "true");
    private final TimerUtil timerUtil = new TimerUtil();
    public KillAura() {
        super("KillAura", Category.COMBAT, 0xff660000);
    }
    @Override
    public void onEnable() {
        super.onEnable();
        timerUtil.reset();
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getType() == EventType.POST) {
            TickRate.update(event);
        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (getMc().world == null || getMc().player == null) return;
        if (event.getEventType() == EventType.PRE) {
            if ( ((swordcheck.isEnabled() && !(getMc().player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSword))))
                return;
            target = getTarget();
            if (target != null) {
                final float[] rots = CombatUtil.getRotationsToEnt(target,getMc().player);
                event.setYaw(rots[0]);
                event.setPitch(rots[1]);
            }
        } else {
            if (target == null || (swordcheck.isEnabled() && !(getMc().player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSword))) return;
            final float ticks = 20.0f - TickRate.TPS;
            final boolean canAttack = getMc().player.getCooledAttackStrength(-ticks) >= 1;
            final ItemStack stack = getMc().player.getHeldItem(EnumHand.OFF_HAND);
            if (canAttack) {
                if (stack != ItemStack.EMPTY && stack.getItem() == Items.SHIELD) {
                    getMc().player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, getMc().player.getHorizontalFacing()));
                }
                getMc().player.connection.sendPacket(new CPacketUseEntity(target));
                getMc().player.swingArm(EnumHand.MAIN_HAND);
                getMc().player.resetCooldown();
            }
        }
    }

    private EntityLivingBase getTarget() {
        double minVal = Double.POSITIVE_INFINITY;
        Entity bestEntity = null;
        for (Entity e : getMc().world.loadedEntityList) {
            double val = getSortingWeight(e);
            if (isValidEntity(e) && val < minVal) {
                minVal = val;
                bestEntity = e;
            }
        }
        return (EntityLivingBase) bestEntity;
    }

    private double getSortingWeight(Entity e) {
        switch (sortMode.getValue()) {
            case FOV:
                return yawDist(e);
            case HEALTH:
                return e instanceof EntityLivingBase ? ((EntityLivingBase) e).getHealth() : Double.POSITIVE_INFINITY;
            default:
                return getMc().player.getDistanceSqToEntity(e);
        }
    }

    private double yawDist(Entity e) {
        if (e != null) {
            final Vec3d difference = e.getPositionVector().addVector(0.0, e.getEyeHeight() / 2.0f, 0.0).subtract(getMc().player.getPositionVector().addVector(0.0, getMc().player.getEyeHeight(), 0.0));
            final double d = Math.abs(getMc().player.rotationYaw - (Math.toDegrees(Math.atan2(difference.z, difference.x)) - 90.0f)) % 360.0f;
            return (d > 180.0f) ? (360.0f - d) : d;
        }
        return 0;
    }

    private boolean isValidEntity(Entity entity) {
        return entity instanceof EntityLivingBase && entity.getEntityId() != -1488 && entity != getMc().player && entity.isEntityAlive() && !Requiem.INSTANCE.getFriendManager().isFriend(entity.getName()) && !(entity.isInvisible() && !invisibles.isEnabled()) && getMc().player.getDistanceSqToEntity(entity) <= range.getValue() * range.getValue() && ((entity instanceof EntityPlayer && players.isEnabled()) || ((entity instanceof EntityMob || entity instanceof EntityGolem) && mobs.isEnabled()) || (entity instanceof IAnimals && animals.isEnabled())) || (passives.isEnabled() && (entity instanceof EntityIronGolem || entity instanceof EntityAmbientCreature));
    }

    private float getLagComp() {
        return -(20 - TickRate.TPS);
    }

    private enum sortmode {
        FOV, HEALTH, DISTANCE
    }
}
