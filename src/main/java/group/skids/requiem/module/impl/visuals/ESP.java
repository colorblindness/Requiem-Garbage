package group.skids.requiem.module.impl.visuals;

import com.google.common.collect.ImmutableMap;
import group.skids.requiem.client.Requiem;
import group.skids.requiem.events.Render2DEvent;
import group.skids.requiem.events.Render3DEvent;
import group.skids.requiem.events.RenderNameEvent;
import group.skids.requiem.mixin.accessors.IRenderManager;
import group.skids.requiem.module.Module;
import group.skids.requiem.utils.GLUProjection;
import group.skids.requiem.utils.RenderUtil;
import group.skids.requiem.utils.value.impl.BooleanValue;
import group.skids.requiem.utils.value.impl.NumberValue;
import net.b0at.api.event.Subscribe;
import net.minecraft.block.BlockChest;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.*;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4f;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ESP extends Module {
    private final BooleanValue nametags = new BooleanValue("Nametags", true);
    private final BooleanValue armor = new BooleanValue("Armor", true);
    private final BooleanValue box = new BooleanValue("Box", true);
    private final BooleanValue health = new BooleanValue("Health", true);
    public BooleanValue skeleton = new BooleanValue("Skeleton", true);
    private final NumberValue<Float> skeletonwidth = new NumberValue<>("Skeleton Width", 1.0f, 0.5f, 10.0f, 0.1f, skeleton, "true");
    private final BooleanValue players = new BooleanValue("Players", true);
    private final BooleanValue mobs = new BooleanValue("Mobs", false);
    private final BooleanValue animals = new BooleanValue("Animals", false);
    private final BooleanValue passives = new BooleanValue("Passives", false);
    private final BooleanValue invisibles = new BooleanValue("Invisibles", true, players, "true");
    private final BooleanValue tiles = new BooleanValue("Tiles", true);
    private final BooleanValue chests = new BooleanValue("Chests", true, tiles, "true");
    private final BooleanValue brewingStands = new BooleanValue("BrewingStands", true, tiles, "true");
    private final BooleanValue furnaces = new BooleanValue("Furnaces", true, tiles, "true");
    private final BooleanValue enchanttable = new BooleanValue("EnchantTable", true, tiles, "true");
    private final BooleanValue shuklers = new BooleanValue("Shuklers", true, tiles, "true");
    private final BooleanValue redstoneBlocks = new BooleanValue("RedstoneBlocks", true, tiles, "true");
    private final BooleanValue spawners = new BooleanValue("Spawners", true, tiles, "true");
    private final Map<EntityPlayer, float[][]> entities = new HashMap<>();
    private final ImmutableMap<String, String> cachedEnchantmentMap = new ImmutableMap.Builder<String, String>()
            .put(Objects.requireNonNull(Enchantment.getEnchantmentByID(0)).getName(), "p").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(10)).getName(), "cob").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(1)).getName(), "fp").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(2)).getName(), "ff").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(3)).getName(), "bp").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(4)).getName(), "pp").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(5)).getName(), "r").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(6)).getName(), "aa").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(7)).getName(), "t").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(8)).getName(), "ds").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(9)).getName(), "fw")
            .put(Objects.requireNonNull(Enchantment.getEnchantmentByID(16)).getName(), "s").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(22)).getName(), "se").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(17)).getName(), "sm").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(18)).getName(), "boa").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(19)).getName(), "kb").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(20)).getName(), "fa").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(21)).getName(), "l")
            .put(Objects.requireNonNull(Enchantment.getEnchantmentByID(32)).getName(), "e").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(33)).getName(), "st").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(35)).getName(), "f")
            .put(Objects.requireNonNull(Enchantment.getEnchantmentByID(48)).getName(), "pow").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(49)).getName(), "pun").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(50)).getName(), "fl").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(51)).getName(), "inf")
            .put(Objects.requireNonNull(Enchantment.getEnchantmentByID(61)).getName(), "lu").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(62)).getName(), "lots")
            .put(Objects.requireNonNull(Enchantment.getEnchantmentByID(34)).getName(), "un").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(70)).getName(), "m").put(Objects.requireNonNull(Enchantment.getEnchantmentByID(71)).getName(), "vc").build();

    public ESP() {
        super("ESP", Category.VISUALS, 0xff3300ff);
    }

    @Subscribe
    public void onRenderNameTag(RenderNameEvent event) {
        if (event.getEntity() instanceof EntityLivingBase && isValid((EntityLivingBase) event.getEntity()))
            event.setCancelled(true);
    }

    @Subscribe
    public void onRender2D(Render2DEvent event) {
        if (getMc().world == null || getMc().player == null) return;
        getMc().world.loadedEntityList.forEach(entity -> {
            if (entity instanceof EntityLivingBase) {
                final EntityLivingBase ent = (EntityLivingBase) entity;
                if (isValid(ent) && entity.getUniqueID() != getMc().player.getUniqueID() && RenderUtil.isInViewFrustrum(ent)) {
                    final Color clr = getEntityColor(entity);
                    double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.getPartialTicks();
                    double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * event.getPartialTicks();
                    double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.getPartialTicks();
                    final AxisAlignedBB bb = entity.getEntityBoundingBox().expand(0.1, 0.1, 0.1);
                    final Vector3d[] corners = {new Vector3d(posX + bb.minX - bb.maxX + entity.width / 2.0f, posY, posZ + bb.minZ - bb.maxZ + entity.width / 2.0f), new Vector3d(posX + bb.maxX - bb.minX - entity.width / 2.0f, posY, posZ + bb.minZ - bb.maxZ + entity.width / 2.0f), new Vector3d(posX + bb.minX - bb.maxX + entity.width / 2.0f, posY, posZ + bb.maxZ - bb.minZ - entity.width / 2.0f), new Vector3d(posX + bb.maxX - bb.minX - entity.width / 2.0f, posY, posZ + bb.maxZ - bb.minZ - entity.width / 2.0f), new Vector3d(posX + bb.minX - bb.maxX + entity.width / 2.0f, posY + bb.maxY - bb.minY, posZ + bb.minZ - bb.maxZ + entity.width / 2.0f), new Vector3d(posX + bb.maxX - bb.minX - entity.width / 2.0f, posY + bb.maxY - bb.minY, posZ + bb.minZ - bb.maxZ + entity.width / 2.0f), new Vector3d(posX + bb.minX - bb.maxX + entity.width / 2.0f, posY + bb.maxY - bb.minY, posZ + bb.maxZ - bb.minZ - entity.width / 2.0f), new Vector3d(posX + bb.maxX - bb.minX - entity.width / 2.0f, posY + bb.maxY - bb.minY, posZ + bb.maxZ - bb.minZ - entity.width / 2.0f)};
                    GLUProjection.Projection result;
                    final Vector4f transformed = new Vector4f(event.getScaledResolution().getScaledWidth() * 2.0f, event.getScaledResolution().getScaledHeight() * 2.0f, -1.0f, -1.0f);
                    for (Vector3d vec : corners) {
                        result = GLUProjection.getInstance().project(vec.x - getMc().getRenderManager().viewerPosX, vec.y - getMc().getRenderManager().viewerPosY, vec.z - getMc().getRenderManager().viewerPosZ, GLUProjection.ClampMode.NONE, true);
                        transformed.setX((float) Math.min(transformed.getX(), result.getX()));
                        transformed.setY((float) Math.min(transformed.getY(), result.getY()));
                        transformed.setW((float) Math.max(transformed.getW(), result.getX()));
                        transformed.setZ((float) Math.max(transformed.getZ(), result.getY()));
                    }
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.scale(.5f, .5f, .5f);
                    final float x = transformed.x * 2;
                    final float w = (transformed.w * 2) - x;
                    final float y = transformed.y * 2;
                    final float h = (transformed.z * 2) - y;
                    if (box.isEnabled()) {
                        RenderUtil.drawBorderedRect(x, y, w, h, 1, 0x00000000, 0xff000000);
                        RenderUtil.drawBorderedRect(x - 1, y - 1, w + 2, h + 2, 1, 0x00000000, clr.getRGB());
                        RenderUtil.drawBorderedRect(x - 2, y - 2, w + 4, h + 4, 1, 0x00000000, 0xff000000);
                    }
                    if (health.isEnabled()) {
                        final float height = (h / ((EntityLivingBase) entity).getMaxHealth()) * Math.min(((EntityLivingBase) entity).getHealth(), ((EntityLivingBase) entity).getMaxHealth());
                        RenderUtil.drawBorderedRect(x - 6, y - 1, 3, h + 2, 1, 0x20000000, 0xff000000);
                        RenderUtil.drawRect(x - 5, y + h, 1, -height, getHealthColor((EntityLivingBase) entity));
                        if (((EntityLivingBase) entity).getMaxHealth() > ((EntityLivingBase) entity).getHealth()) {
                            getMc().fontRenderer.drawStringWithShadow((int) ((EntityLivingBase) entity).getHealth() + "hp", x - 6 - getMc().fontRenderer.getStringWidth((int) ((EntityLivingBase) entity).getHealth() + "hp"), y + h - height, -1);
                        }
                    }
                    if (nametags.isEnabled()) {
                        RenderUtil.drawBorderedRect((x + (w / 2) - (getMc().fontRenderer.getStringWidth(Requiem.INSTANCE.getFriendManager().isFriend(ent.getName()) ? (Requiem.INSTANCE.getFriendManager().getFriend(ent.getName()).getAlias() != null ? Requiem.INSTANCE.getFriendManager().getFriend(ent.getName()).getAlias() : ent.getName()) : ent.getName()) >> 1)) - 2,y - 5 - getMc().fontRenderer.FONT_HEIGHT,getMc().fontRenderer.getStringWidth(Requiem.INSTANCE.getFriendManager().isFriend(ent.getName()) ? (Requiem.INSTANCE.getFriendManager().getFriend(ent.getName()).getAlias() != null ? Requiem.INSTANCE.getFriendManager().getFriend(ent.getName()).getAlias() : ent.getName()) : ent.getName()) + 3,getMc().fontRenderer.FONT_HEIGHT + 3,1,0x80000000,0x60000000);
                        getMc().fontRenderer.drawStringWithShadow(Requiem.INSTANCE.getFriendManager().isFriend(ent.getName()) ? (Requiem.INSTANCE.getFriendManager().getFriend(ent.getName()).getAlias() != null ? Requiem.INSTANCE.getFriendManager().getFriend(ent.getName()).getAlias() : ent.getName()) : ent.getName(), (x + (w / 2) - (getMc().fontRenderer.getStringWidth(Requiem.INSTANCE.getFriendManager().isFriend(ent.getName()) ? (Requiem.INSTANCE.getFriendManager().getFriend(ent.getName()).getAlias() != null ? Requiem.INSTANCE.getFriendManager().getFriend(ent.getName()).getAlias() : ent.getName()) : ent.getName()) >> 1)), y - 3 - getMc().fontRenderer.FONT_HEIGHT, clr.getRGB());
                    }
                    if (armor.isEnabled() && ent instanceof EntityPlayer)
                        drawArmor((EntityPlayer) ent, (int) (x + w / 2), (int) (y - 1 - (getMc().fontRenderer.FONT_HEIGHT * (nametags.isEnabled() ? 3.15 : 2))));
                    GlStateManager.scale(1.0f, 1.0f, 1.0f);
                    GlStateManager.popMatrix();
                }
            }
        });
    }

    @Subscribe
    public void onRenderHand(Render3DEvent event) {
        if (skeleton.isEnabled()) {
            startEnd(true);
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glDisable(2848);
            entities.keySet().removeIf(this::doesntContain);
            getMc().world.playerEntities.forEach(player -> drawSkeleton(event, player));
            Gui.drawRect(0, 0, 0, 0, 0);
            startEnd(false);
        }
        if (chests.isEnabled() || brewingStands.isEnabled() || furnaces.isEnabled() || enchanttable.isEnabled() || shuklers.isEnabled() || redstoneBlocks.isEnabled() || spawners.isEnabled()) {
            getMc().world.loadedTileEntityList.forEach(tile -> {
                final double posX = tile.getPos().getX() - ((IRenderManager) getMc().getRenderManager()).getRenderPosX();
                final double posY = tile.getPos().getY() - ((IRenderManager) getMc().getRenderManager()).getRenderPosY();
                final double posZ = tile.getPos().getZ() - ((IRenderManager) getMc().getRenderManager()).getRenderPosZ();
                if (tile instanceof TileEntityChest) {
                    if (chests.isEnabled()) {
                        AxisAlignedBB bb = new AxisAlignedBB(0.0625, 0.0, 0.0625, 1, 0.875, 1).offset(posX, posY, posZ).contract(0.0625, 0, 0.0625);
                        TileEntityChest adjacent = null;
                        if (((TileEntityChest) tile).adjacentChestXNeg != null)
                            adjacent = ((TileEntityChest) tile).adjacentChestXNeg;
                        if (((TileEntityChest) tile).adjacentChestXPos != null)
                            adjacent = ((TileEntityChest) tile).adjacentChestXPos;
                        if (((TileEntityChest) tile).adjacentChestZNeg != null)
                            adjacent = ((TileEntityChest) tile).adjacentChestZNeg;
                        if (((TileEntityChest) tile).adjacentChestZPos != null)
                            adjacent = ((TileEntityChest) tile).adjacentChestZPos;
                        if (adjacent != null) {
                            bb = bb.union(new AxisAlignedBB(0.0625, 0.0, 0.0625, 1, 0.875, 1).offset(adjacent.getPos().getX() - ((IRenderManager) getMc().getRenderManager()).getRenderPosX(), adjacent.getPos().getY() - ((IRenderManager) getMc().getRenderManager()).getRenderPosY(), adjacent.getPos().getZ() - ((IRenderManager) getMc().getRenderManager()).getRenderPosZ())).contract(0.0625, 0, 0.0625);
                        }
                        if (((TileEntityChest) tile).getChestType() == BlockChest.Type.TRAP) {
                            RenderUtil.drawESP(bb, 255f, 91f, 86f, 40F);
                            RenderUtil.drawESPOutline(bb, 255f, 91f, 86f, 255f, 1f);
                        } else {
                            RenderUtil.drawESP(bb, 255f, 227f, 0f, 40F);
                            RenderUtil.drawESPOutline(bb, 255f, 227f, 0f, 255f, 1f);
                        }
                    }
                }
                if (tile instanceof TileEntityEnderChest) {
                    if (chests.isEnabled()) {
                        RenderUtil.drawESP(new AxisAlignedBB(0.0625, 0.0, 0.0625, 1, 0.875, 1).offset(posX, posY, posZ).contract(0.0625, 0, 0.0625), 78f, 197f, 255f, 40F);
                        RenderUtil.drawESPOutline(new AxisAlignedBB(0.0625, 0.0, 0.0625, 1, 0.875, 1).offset(posX, posY, posZ).contract(0.0625, 0, 0.0625), 78f, 197f, 255f, 255f, 1f);
                    }
                }
                if (tile instanceof TileEntityBrewingStand) {
                    if (brewingStands.isEnabled()) {
                        RenderUtil.drawESP(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(posX, posY, posZ), 234f, 255f, 96f, 40F);
                        RenderUtil.drawESPOutline(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(posX, posY, posZ), 234f, 255f, 96f, 255f, 1f);
                    }
                }
                if (tile instanceof TileEntityFurnace) {
                    if (furnaces.isEnabled()) {
                        RenderUtil.drawESP(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(posX, posY, posZ), 254f, 124f, 0f, 40f);
                        RenderUtil.drawESPOutline(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(posX, posY, posZ), 254f, 124f, 0f, 255f, 1f);
                    }
                }
                if (tile instanceof TileEntityEnchantmentTable) {
                    if (enchanttable.isEnabled()) {
                        RenderUtil.drawESP(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(posX, posY, posZ), 255F, 90, 12F, 40F);
                        RenderUtil.drawESPOutline(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(posX, posY, posZ), 255F, 90, 12F, 255f, 1f);
                    }
                }
                if (tile instanceof TileEntityShulkerBox) {
                    if (shuklers.isEnabled()) {
                        final Color shulkercolor = new Color(((TileEntityShulkerBox) tile).getColor().getColorValue());
                        RenderUtil.drawESP(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(posX, posY, posZ), shulkercolor.getRed(), shulkercolor.getGreen(), shulkercolor.getBlue(), 40F);
                        RenderUtil.drawESPOutline(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(posX, posY, posZ), shulkercolor.getRed(), shulkercolor.getGreen(), shulkercolor.getBlue(), 255f, 1f);
                    }
                }
                if (tile instanceof TileEntityHopper || tile instanceof TileEntityDispenser) {
                    if (redstoneBlocks.isEnabled()) {
                        RenderUtil.drawESP(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(posX, posY, posZ), 161f, 161f, 161f, 40F);
                        RenderUtil.drawESPOutline(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(posX, posY, posZ), 161f, 161f, 161f, 255f, 1f);
                    }
                }
                if (tile instanceof TileEntityMobSpawner) {
                    if (spawners.isEnabled()) {
                        final Color rainbow = new Color(RenderUtil.getRainbow(3000, 0, 0.85f));
                        RenderUtil.drawESP(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(posX, posY, posZ), rainbow.getRed(), rainbow.getGreen(), rainbow.getBlue(), 40F);
                        RenderUtil.drawESPOutline(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(posX, posY, posZ), rainbow.getRed(), rainbow.getGreen(), rainbow.getBlue(), 255f, 1f);
                    }
                }
            });
        }
    }

    private boolean isValid(EntityLivingBase entity) {
        return getMc().player != entity && entity.getEntityId() != -1488 && isValidType(entity) && entity.isEntityAlive() && (!entity.isInvisible() || invisibles.isEnabled());
    }

    private boolean isValidType(EntityLivingBase entity) {
        return (players.isEnabled() && entity instanceof EntityPlayer) || ((mobs.isEnabled() && (entity instanceof EntityMob || entity instanceof EntitySlime)) || (passives.isEnabled() && (entity instanceof EntityVillager || entity instanceof EntityGolem)) || (animals.isEnabled() && entity instanceof IAnimals));
    }

    private int getHealthColor(EntityLivingBase player) {
        return Color.HSBtoRGB(Math.max(0.0F, Math.min(player.getHealth(), player.getMaxHealth()) / player.getMaxHealth()) / 3.0F, 1.0F, 0.8f) | 0xFF000000;
    }

    private Color getEntityColor(Entity entity) {
        return new Color(Requiem.INSTANCE.getFriendManager().isFriend(entity.getName()) ? 0xff2020ff : (entity.isSneaking() ? 0xffffff00 : 0xffffffff));
    }

    private void drawSkeleton(Render3DEvent event, EntityPlayer e) {
        final Color clr = getEntityColor(e);
        float[][] entPos = entities.get(e);
        if (entPos != null && e.getEntityId() != -1488 && e.isEntityAlive() && RenderUtil.isInViewFrustrum(e) && !e.isDead && e != getMc().player && !e.isPlayerSleeping()) {
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glLineWidth(skeletonwidth.getValue());
            GlStateManager.color(clr.getRed() / 255.f, clr.getGreen() / 255.f, clr.getBlue() / 255.f, 1);
            Vec3d vec = getVec3(event, e);
            double x = vec.x - ((IRenderManager) getMc().getRenderManager()).getRenderPosX();
            double y = vec.y - ((IRenderManager) getMc().getRenderManager()).getRenderPosY();
            double z = vec.z - ((IRenderManager) getMc().getRenderManager()).getRenderPosZ();
            GL11.glTranslated(x, y, z);
            float xOff = e.prevRenderYawOffset + (e.renderYawOffset - e.prevRenderYawOffset) * event.getPartialTicks();
            GL11.glRotatef(-xOff, 0.0F, 1.0F, 0.0F);
            GL11.glTranslated(0.0D, 0.0D, e.isSneaking() ? -0.235D : 0.0D);
            float yOff = e.isSneaking() ? 0.6F : 0.75F;
            GL11.glPushMatrix();
            GlStateManager.color(clr.getRed() / 255.f, clr.getGreen() / 255.f, clr.getBlue() / 255.f, 1);
            GL11.glTranslated(-0.125D, yOff, 0.0D);
            if (entPos[3][0] != 0.0F) {
                GL11.glRotatef(entPos[3][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
            }

            if (entPos[3][1] != 0.0F) {
                GL11.glRotatef(entPos[3][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
            }

            if (entPos[3][2] != 0.0F) {
                GL11.glRotatef(entPos[3][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
            }

            GL11.glBegin(3);
            GL11.glVertex3d(0.0D, 0.0D, 0.0D);
            GL11.glVertex3d(0.0D, (-yOff), 0.0D);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GlStateManager.color(clr.getRed() / 255.f, clr.getGreen() / 255.f, clr.getBlue() / 255.f, 1);
            GL11.glTranslated(0.125D, yOff, 0.0D);
            if (entPos[4][0] != 0.0F) {
                GL11.glRotatef(entPos[4][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
            }

            if (entPos[4][1] != 0.0F) {
                GL11.glRotatef(entPos[4][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
            }

            if (entPos[4][2] != 0.0F) {
                GL11.glRotatef(entPos[4][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
            }

            GL11.glBegin(3);
            GL11.glVertex3d(0.0D, 0.0D, 0.0D);
            GL11.glVertex3d(0.0D, (-yOff), 0.0D);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glTranslated(0.0D, 0.0D, e.isSneaking() ? 0.25D : 0.0D);
            GL11.glPushMatrix();
            GlStateManager.color(clr.getRed() / 255.f, clr.getGreen() / 255.f, clr.getBlue() / 255.f, 1);
            GL11.glTranslated(0.0D, e.isSneaking() ? -0.05D : 0.0D, e.isSneaking() ? -0.01725D : 0.0D);
            GL11.glPushMatrix();
            GlStateManager.color(clr.getRed() / 255.f, clr.getGreen() / 255.f, clr.getBlue() / 255.f, 1);
            GL11.glTranslated(-0.375D, yOff + 0.55D, 0.0D);
            if (entPos[1][0] != 0.0F) {
                GL11.glRotatef(entPos[1][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
            }

            if (entPos[1][1] != 0.0F) {
                GL11.glRotatef(entPos[1][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
            }

            if (entPos[1][2] != 0.0F) {
                GL11.glRotatef(-entPos[1][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
            }

            GL11.glBegin(3);
            GL11.glVertex3d(0.0D, 0.0D, 0.0D);
            GL11.glVertex3d(0.0D, -0.5D, 0.0D);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslated(0.375D, yOff + 0.55D, 0.0D);
            if (entPos[2][0] != 0.0F) {
                GL11.glRotatef(entPos[2][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
            }

            if (entPos[2][1] != 0.0F) {
                GL11.glRotatef(entPos[2][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
            }

            if (entPos[2][2] != 0.0F) {
                GL11.glRotatef(-entPos[2][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
            }

            GL11.glBegin(3);
            GL11.glVertex3d(0.0D, 0.0D, 0.0D);
            GL11.glVertex3d(0.0D, -0.5D, 0.0D);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glRotatef(xOff - e.rotationYawHead, 0.0F, 1.0F, 0.0F);
            GL11.glPushMatrix();
            GlStateManager.color(clr.getRed() / 255.f, clr.getGreen() / 255.f, clr.getBlue() / 255.f, 1);
            GL11.glTranslated(0.0D, yOff + 0.55D, 0.0D);
            if (entPos[0][0] != 0.0F) {
                GL11.glRotatef(entPos[0][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
            }

            GL11.glBegin(3);
            GL11.glVertex3d(0.0D, 0.0D, 0.0D);
            GL11.glVertex3d(0.0D, 0.3D, 0.0D);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPopMatrix();
            GL11.glRotatef(e.isSneaking() ? 25.0F : 0.0F, 1.0F, 0.0F, 0.0F);
            GL11.glTranslated(0.0D, e.isSneaking() ? -0.16175D : 0.0D, e.isSneaking() ? -0.48025D : 0.0D);
            GL11.glPushMatrix();
            GL11.glTranslated(0.0D, yOff, 0.0D);
            GL11.glBegin(3);
            GL11.glVertex3d(-0.125D, 0.0D, 0.0D);
            GL11.glVertex3d(0.125D, 0.0D, 0.0D);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GlStateManager.color(clr.getRed() / 255.f, clr.getGreen() / 255.f, clr.getBlue() / 255.f, 1);
            GL11.glTranslated(0.0D, yOff, 0.0D);
            GL11.glBegin(3);
            GL11.glVertex3d(0.0D, 0.0D, 0.0D);
            GL11.glVertex3d(0.0D, 0.55D, 0.0D);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslated(0.0D, yOff + 0.55D, 0.0D);
            GL11.glBegin(3);
            GL11.glVertex3d(-0.375D, 0.0D, 0.0D);
            GL11.glVertex3d(0.375D, 0.0D, 0.0D);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPopMatrix();
        }
    }


    private void drawArmor(EntityPlayer player, int x, int y) {
        if (!player.inventory.armorInventory.isEmpty()) {
            List<ItemStack> items = new ArrayList<>();
            if (player.getHeldItem(EnumHand.OFF_HAND) != ItemStack.EMPTY) {
                items.add(player.getHeldItem(EnumHand.OFF_HAND));
            }
            if (player.getHeldItem(EnumHand.MAIN_HAND) != ItemStack.EMPTY) {
                items.add(player.getHeldItem(EnumHand.MAIN_HAND));
            }
            for (int index = 3; index >= 0; index--) {
                ItemStack stack = player.inventory.armorInventory.get(index);
                if (stack != ItemStack.EMPTY) {
                    items.add(stack);
                }
            }
            int armorX = x - ((items.size() * 18) / 2);
            for (ItemStack stack : items) {
                GlStateManager.pushMatrix();
                GlStateManager.enableLighting();
                getMc().getRenderItem().renderItemIntoGUI(stack, armorX, y);
                getMc().getRenderItem().renderItemOverlayIntoGUI(getMc().fontRenderer, stack, armorX, y, "");
                GlStateManager.disableLighting();
                GlStateManager.popMatrix();
                GlStateManager.disableDepth();
                if (stack.isStackable() && stack.getCount() > 0) {
                    getMc().fontRenderer.drawStringWithShadow(String.valueOf(stack.getCount()), armorX + 4, y + 8, 0xDDD1E6);
                }
                NBTTagList enchants = stack.getEnchantmentTagList();
                GlStateManager.pushMatrix();
                if (stack.getItem() == Items.GOLDEN_APPLE && stack.getMetadata() == 1) {
                    getMc().fontRenderer.drawStringWithShadow("op", armorX, y, 0xFFFF0000);
                }
                if (!enchants.hasNoTags()) {
                    int ency = y + 4;
                    for (NBTBase nbtBase : enchants) {
                        if (nbtBase.getId() == 10) {
                            NBTTagCompound nbtTagCompound = (NBTTagCompound) nbtBase;
                            short id = nbtTagCompound.getShort("id");
                            short level = nbtTagCompound.getShort("lvl");
                            Enchantment enc = Enchantment.getEnchantmentByID(id);

                            if (enc != null) {
                                String encName = cachedEnchantmentMap.get(enc.getName()) + level;
                                getMc().fontRenderer.drawStringWithShadow(encName, armorX + 4, ency, enc.isCurse() ? 0xff9999 : 0xDDD1E6);
                                ency -= 8;
                            }
                        }
                    }
                }
                GlStateManager.enableDepth();
                GlStateManager.popMatrix();
                armorX += 18;
            }
        }
    }

    private Vec3d getVec3(Render3DEvent event, EntityPlayer var0) {
        float pt = event.getPartialTicks();
        double x = var0.lastTickPosX + (var0.posX - var0.lastTickPosX) * pt;
        double y = var0.lastTickPosY + (var0.posY - var0.lastTickPosY) * pt;
        double z = var0.lastTickPosZ + (var0.posZ - var0.lastTickPosZ) * pt;
        return new Vec3d(x, y, z);
    }

    public void addEntity(EntityPlayer e, ModelPlayer model) {
        entities.put(e, new float[][]{{model.bipedHead.rotateAngleX, model.bipedHead.rotateAngleY, model.bipedHead.rotateAngleZ}, {model.bipedRightArm.rotateAngleX, model.bipedRightArm.rotateAngleY, model.bipedRightArm.rotateAngleZ}, {model.bipedLeftArm.rotateAngleX, model.bipedLeftArm.rotateAngleY, model.bipedLeftArm.rotateAngleZ}, {model.bipedRightLeg.rotateAngleX, model.bipedRightLeg.rotateAngleY, model.bipedRightLeg.rotateAngleZ}, {model.bipedLeftLeg.rotateAngleX, model.bipedLeftLeg.rotateAngleY, model.bipedLeftLeg.rotateAngleZ}});
    }

    private boolean doesntContain(EntityPlayer var0) {
        return !getMc().world.playerEntities.contains(var0);
    }

    private void startEnd(boolean revert) {
        if (revert) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GL11.glEnable(2848);
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();
            GL11.glHint(3154, 4354);
        } else {
            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
            GL11.glDisable(2848);
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
        GlStateManager.depthMask(!revert);
    }
}