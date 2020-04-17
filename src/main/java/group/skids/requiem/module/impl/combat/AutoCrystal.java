package group.skids.requiem.module.impl.combat;

import group.skids.requiem.client.Requiem;
import group.skids.requiem.events.PacketEvent;
import group.skids.requiem.events.Render3DEvent;
import group.skids.requiem.events.UpdateEvent;
import group.skids.requiem.mixin.accessors.IRenderManager;
import group.skids.requiem.module.Module;
import group.skids.requiem.utils.MathUtils;
import group.skids.requiem.utils.RenderUtil;
import group.skids.requiem.utils.TimerUtil;
import group.skids.requiem.utils.value.impl.BooleanValue;
import group.skids.requiem.utils.value.impl.NumberValue;
import io.netty.util.internal.MathUtil;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AutoCrystal extends Module {
    private final NumberValue<Float> targetRange = new NumberValue<>("Target Range", 6.0f, 1.0f, 6.0f, 0.1f);
    private final BooleanValue placeCrystal = new BooleanValue("Place", true);
    private final NumberValue<Float> placeRange = new NumberValue<>("Place Range", 6.0f, 1.0f, 6.0f, 0.1f, placeCrystal, "true");
    private final NumberValue<Integer> placeDelay = new NumberValue<>("Place Delay", 200, 0, 1000, 1, placeCrystal, "true");
    private final BooleanValue breakCrystal = new BooleanValue("Break", true);
    private final NumberValue<Float> breakRange = new NumberValue<>("Break Range", 6.0f, 1.0f, 6.0f, 0.1f, breakCrystal, "true");
    private final NumberValue<Integer> breakDelay = new NumberValue<>("Break Delay", 200, 0, 1000, 1, breakCrystal, "true");
    private final BooleanValue throughWall = new BooleanValue("Through Wall", true);
    private final BooleanValue autoSwitch = new BooleanValue("Auto Switch", true);
    private final BooleanValue antiWeakness = new BooleanValue("Anti Weakness", true);
    private final BooleanValue noDesync = new BooleanValue("No Desync", true);
    private final BooleanValue noGappleSwitch = new BooleanValue("No Gapple Switch", true);
    private final BooleanValue showRotations = new BooleanValue("Show Rotations", false);
    private final BooleanValue players = new BooleanValue("Players", true);
    private final BooleanValue invisibles = new BooleanValue("Invisibles", true, players, "true");
    private final BooleanValue mobs = new BooleanValue("Mobs", false);
    private final BooleanValue animals = new BooleanValue("Animals", false);
    private final BooleanValue passives = new BooleanValue("Passives", false);
    private final NumberValue<Integer> minimumDamage = new NumberValue<>("Minimum Damage", 4, 0, 40, 1);
    private final NumberValue<Integer> maxSelfDamage = new NumberValue<>("Max Self Damage", 6, 0, 40, 1);
    private final TimerUtil placeTimer = new TimerUtil();
    private final TimerUtil breakTimer = new TimerUtil();
    private BlockPos render;
    private boolean switchCooldown = false;
    private boolean isAttacking = false;
    private int oldSlot = -1;
    private int waitCounter;
    private String dmg = null;

    public AutoCrystal() {
        super("AutoCrystal", Category.COMBAT, 0xffff0022);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (getMc().world == null || getMc().player == null) return;
        final EntityEnderCrystal crystal = getMc().world.loadedEntityList.stream().filter(e -> e instanceof EntityEnderCrystal && (getMc().player.canEntityBeSeen(e) || throughWall.isEnabled()) && getMc().player.getDistanceToEntity(e) <= breakRange.getValue()).map(entity -> (EntityEnderCrystal) entity).min(Comparator.comparing(c -> getMc().player.getDistanceToEntity(c))).orElse(null);
        if (breakCrystal.isEnabled() && crystal != null) {
            if (!breakTimer.sleep(breakDelay.getValue())) return;
            if (antiWeakness.getValue() && getMc().player.isPotionActive(MobEffects.WEAKNESS)) {
                if (!isAttacking) {
                    oldSlot = getMc().player.inventory.currentItem;
                    isAttacking = true;
                }
                int newSlot = -1;
                for (int i = 0; i < 9; i++) {
                    ItemStack stack = getMc().player.inventory.getStackInSlot(i);
                    if (stack == ItemStack.EMPTY) {
                        continue;
                    }
                    if ((stack.getItem() instanceof ItemSword)) {
                        newSlot = i;
                        break;
                    }
                    if ((stack.getItem() instanceof ItemTool)) {
                        newSlot = i;
                        break;
                    }
                }
                if (newSlot != -1) {
                    getMc().player.inventory.currentItem = newSlot;
                    switchCooldown = true;
                }
            }
            final float[] rots = MathUtils.calcAngle(new Vec3d(getMc().player.posX, getMc().player.posY + getMc().player.getEyeHeight(), getMc().player.posZ), new Vec3d(crystal.posX, crystal.posY, crystal.posZ));
            if (showRotations.isEnabled()) {
                getMc().player.rotationYaw = rots[0];
                getMc().player.rotationPitch = rots[1];
            } else {
                event.setYaw(rots[0]);
                event.setPitch(rots[1]);
            }
            getMc().playerController.attackEntity(getMc().player, crystal);
            getMc().player.swingArm(EnumHand.MAIN_HAND);
            return;
        } else {
            if (oldSlot != -1) {
                getMc().player.inventory.currentItem = oldSlot;
                oldSlot = -1;
            }
            isAttacking = false;
        }

        int crystalSlot = getMc().player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL ? getMc().player.inventory.currentItem : -1;
        if (crystalSlot == -1) {
            for (int l = 0; l < 9; ++l) {
                if (getMc().player.inventory.getStackInSlot(l).getItem() == Items.END_CRYSTAL) {
                    crystalSlot = l;
                    break;
                }
            }
        }
        final boolean offhand = getMc().player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        if (!offhand && crystalSlot == -1) return;
        final List<BlockPos> blocks = findCrystalBlocks();
        final List<Entity> entities = getMc().world.loadedEntityList.stream().filter(this::isValidEntity).sorted(Comparator.comparing(e -> getMc().player.getDistanceToEntity(e))).collect(Collectors.toList());
        BlockPos placePosition = null;
        double damage = -1;
        for (final Entity entity : entities) {
            if (entity == getMc().player) continue;
            if (((EntityLivingBase) entity).getHealth() <= 0 || entity.isDead || getMc().player == null) {
                continue;
            }
            for (BlockPos blockPos : blocks) {
                double b = entity.getDistanceSq(blockPos);
                double d = calculateDamage(blockPos.getX() + .5, blockPos.getY() + 1, blockPos.getZ() + .5, entity);
                if (b >= placeRange.getValue() * placeRange.getValue() || (d < minimumDamage.getValue() && ((EntityLivingBase) entity).getHealth() + ((EntityLivingBase) entity).getAbsorptionAmount() > minimumDamage.getValue()))
                    continue;
                if (d > damage) {
                    double self = calculateDamage(blockPos.getX() + .5, blockPos.getY() + 1, blockPos.getZ() + .5, getMc().player);
                    if ((self > d && !(d < ((EntityLivingBase) entity).getHealth())) || self - .5 > getMc().player.getHealth() || self > maxSelfDamage.getValue())
                        continue;
                    damage = d;
                    placePosition = blockPos;
                }
            }
        }
        if (damage == -1) return;
        render = placePosition;
        if (placeCrystal.isEnabled()) {
            if (getMc().player == null) return;
            final float[] rots = MathUtils.calcAngle(new Vec3d(getMc().player.posX, getMc().player.posY + getMc().player.getEyeHeight(), getMc().player.posZ), new Vec3d(placePosition.getX() + 0.5, placePosition.getY() - 0.5, placePosition.getZ() + 0.5));
            if (showRotations.isEnabled()) {
                getMc().player.rotationYaw = rots[0];
                getMc().player.rotationPitch = rots[1];
            } else {
                event.setYaw(rots[0]);
                event.setPitch(rots[1]);
            }
            final RayTraceResult result = getMc().world.rayTraceBlocks(new Vec3d(getMc().player.posX, getMc().player.posY + getMc().player.getEyeHeight(), getMc().player.posZ), new Vec3d(placePosition.getX() + .5, placePosition.getY() - .5d, placePosition.getZ() + .5));
            final EnumFacing facing = result == null || result.sideHit == null ? null : result.sideHit;
            if (!offhand && getMc().player.inventory.currentItem != crystalSlot) {
                if (autoSwitch.isEnabled()) {
                    if (noGappleSwitch.isEnabled() && isEatingGap()) {
                        return;
                    } else {
                        getMc().player.inventory.currentItem = crystalSlot;
                        switchCooldown = true;
                    }
                }
                return;
            }
            if (switchCooldown) {
                switchCooldown = false;
                return;
            }
            if (getMc().player != null && facing != null && placeTimer.sleep(placeDelay.getValue())) {
                getMc().player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(placePosition, facing, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
                dmg = Math.floor(damage) + "hp";
            }
        }
    }

    @Subscribe
    public void onRender3D(Render3DEvent event) {
        if (render != null) {
            final AxisAlignedBB bb = new AxisAlignedBB(render.getX() - getMc().getRenderManager().viewerPosX, render.getY() - getMc().getRenderManager().viewerPosY, render.getZ() - getMc().getRenderManager().viewerPosZ, render.getX() + 1 - getMc().getRenderManager().viewerPosX, render.getY() + 1 - getMc().getRenderManager().viewerPosY, render.getZ() + 1 - getMc().getRenderManager().viewerPosZ);
            if (RenderUtil.isInViewFrustrum(new AxisAlignedBB(bb.minX + getMc().getRenderManager().viewerPosX, bb.minY + getMc().getRenderManager().viewerPosY, bb.minZ + getMc().getRenderManager().viewerPosZ, bb.maxX + getMc().getRenderManager().viewerPosX, bb.maxY + getMc().getRenderManager().viewerPosY, bb.maxZ + getMc().getRenderManager().viewerPosZ))) {
                RenderUtil.drawESP(bb, 255, 0, 0, 40F);
                RenderUtil.drawESPOutline(bb, 255, 0, 0, 255f, 1f);
                final double posX = render.getX() - ((IRenderManager) getMc().getRenderManager()).getRenderPosX();
                final double posY = render.getY() - ((IRenderManager) getMc().getRenderManager()).getRenderPosY();
                final double posZ = render.getZ() - ((IRenderManager) getMc().getRenderManager()).getRenderPosZ();
                RenderUtil.renderTag(dmg, posX + 0.5, posY - 0.25f, posZ + 0.5, new Color(255, 0, 0).getRGB());
                GlStateManager.enableDepth();
                GlStateManager.depthMask(true);
                GlStateManager.enableLighting();
                GlStateManager.disableBlend();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                RenderHelper.disableStandardItemLighting();
            }
        }
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof SPacketSoundEffect && noDesync.isEnabled()) {
            final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for (Entity e : getMc().world.loadedEntityList) {
                    if (e instanceof EntityEnderCrystal) {
                        if (e.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0f) {
                            e.setDead();
                        }
                    }
                }
            }
        }
    }

    private boolean isValidEntity(Entity entity) {
        return entity instanceof EntityLivingBase && entity.getEntityId() != -1488 && entity != getMc().player && entity.isEntityAlive() && !Requiem.INSTANCE.getFriendManager().isFriend(entity.getName()) && !(entity.isInvisible() && !invisibles.isEnabled()) && getMc().player.getDistanceSqToEntity(entity) <= targetRange.getValue() * targetRange.getValue() && ((entity instanceof EntityPlayer && players.isEnabled()) || ((entity instanceof EntityMob || entity instanceof EntityGolem) && mobs.isEnabled()) || (entity instanceof IAnimals && animals.isEnabled())) || (passives.isEnabled() && (entity instanceof EntityIronGolem || entity instanceof EntityAmbientCreature));
    }

    private boolean isEatingGap() {
        return getMc().player.getHeldItemMainhand().getItem() instanceof ItemAppleGold && getMc().player.isHandActive();
    }

    private boolean canPlaceCrystal(BlockPos blockPos) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);
        return (getMc().world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK
                || getMc().world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN)
                && getMc().world.getBlockState(boost).getBlock() == Blocks.AIR
                && getMc().world.getBlockState(boost2).getBlock() == Blocks.AIR
                && getMc().world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty()
                && getMc().world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
    }

    private List<BlockPos> findCrystalBlocks() {
        final NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(getSphere(getMc().player.getPosition(), placeRange.getValue(), placeRange.getValue(), false, true, 0).stream().filter(this::canPlaceCrystal).collect(Collectors.toList()));
        return positions;
    }

    private List<BlockPos> getSphere(BlockPos loc, float r, Float h, boolean hollow, boolean sphere, int plus_y) {
        final List<BlockPos> circleblocks = new ArrayList<>();
        final int cx = loc.getX();
        final int cy = loc.getY();
        final int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }
            }
        }
        return circleblocks;
    }

    private float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        final float doubleExplosionSize = 12.0F;
        final double distancedsize = entity.getDistance(posX, posY, posZ) / (double) doubleExplosionSize;
        final Vec3d vec3d = new Vec3d(posX, posY, posZ);
        final double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        final double v = (1.0D - distancedsize) * blockDensity;
        final float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D));
        double finald = 1.0D;
        if (entity instanceof EntityLivingBase) {
            finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(getMc().world, null, posX, posY, posZ, 6F, false, true));
        }
        return (float) finald;
    }

    private float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

            int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            float f = MathHelper.clamp(k, 0.0F, 20.0F);
            damage *= 1.0F - f / 25.0F;
            if (entity.isPotionActive(Objects.requireNonNull(Potion.getPotionById(11)))) {
                damage = damage - (damage / 4);
            }
            return damage;
        } else {
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            return damage;
        }
    }

    private float getDamageMultiplied(float damage) {
        int diff = getMc().world.getDifficulty().getDifficultyId();
        return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
    }
}
