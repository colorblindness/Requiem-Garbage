package group.skids.requiem.utils;

import group.skids.requiem.mixin.accessors.IRenderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector3d;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

public class RenderUtil {
    private final static Frustum frustrum = new Frustum();
    private final static FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
    private final static IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final static FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
    private final static FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    public static double interpolate(double current, double old, double scale) {
        return old + (current - old) * scale;
    }

    public static int getRainbow(int speed, int offset,float s) {
        float hue = (System.currentTimeMillis() + offset) % speed;
        hue /= speed;
        return Color.getHSBColor(hue, s, 1f).getRGB();
    }

    public static void glBillboard(float x, float y, float z) {
        float scale = 0.016666668f * 1.6f;
        GlStateManager.translate(x - ((IRenderManager) Minecraft.getMinecraft().getRenderManager()).getRenderPosX(), y - ((IRenderManager)Minecraft.getMinecraft().getRenderManager()).getRenderPosY(), z - ((IRenderManager)Minecraft.getMinecraft().getRenderManager()).getRenderPosZ());
        GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-Minecraft.getMinecraft().player.rotationYaw, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(Minecraft.getMinecraft().player.rotationPitch, Minecraft.getMinecraft().gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);
    }
    public static void glBillboardDistanceScaled(float x, float y, float z, EntityPlayer player, float scale) {
        glBillboard(x, y, z);
        int distance = (int) player.getDistance(x, y, z);
        float scaleDistance = (distance / 2.0f) / (2.0f + (2.0f - scale));
        if (scaleDistance < 1f)
            scaleDistance = 1;
        GlStateManager.scale(scaleDistance, scaleDistance, scaleDistance);
    }
    public static void drawBox(AxisAlignedBB boundingBox) {
        if (boundingBox == null) {
            return;
        }

        GlStateManager.glBegin(GL11.GL_QUADS);
        GlStateManager.glVertex3f((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ);
        GlStateManager.glVertex3f((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ);
        GlStateManager.glVertex3f((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
        GlStateManager.glVertex3f((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
        GlStateManager.glEnd();

        GlStateManager.glBegin(GL11.GL_QUADS);
        GlStateManager.glVertex3f((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ);
        GlStateManager.glVertex3f((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ);
        GlStateManager.glVertex3f((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
        GlStateManager.glVertex3f((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
        GlStateManager.glEnd();

        GlStateManager.glBegin(GL11.GL_QUADS);
        GlStateManager.glVertex3f((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ);
        GlStateManager.glVertex3f((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ);
        GlStateManager.glVertex3f((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
        GlStateManager.glVertex3f((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ);
        GlStateManager.glEnd();

        GlStateManager.glBegin(GL11.GL_QUADS);
        GlStateManager.glVertex3f((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ);
        GlStateManager.glVertex3f((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ);
        GlStateManager.glVertex3f((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ);
        GlStateManager.glVertex3f((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
        GlStateManager.glEnd();

        GlStateManager.glBegin(GL11.GL_QUADS);
        GlStateManager.glVertex3f((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ);
        GlStateManager.glVertex3f((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ);
        GlStateManager.glVertex3f((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ);
        GlStateManager.glVertex3f((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
        GlStateManager.glEnd();

        GlStateManager.glBegin(GL11.GL_QUADS);
        GlStateManager.glVertex3f((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ);
        GlStateManager.glVertex3f((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ);
        GlStateManager.glVertex3f((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
        GlStateManager.glVertex3f((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ);
        GlStateManager.glEnd();

        GlStateManager.glBegin(GL11.GL_QUADS);
        GlStateManager.glVertex3f((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ);
        GlStateManager.glVertex3f((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ);
        GlStateManager.glVertex3f((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ);
        GlStateManager.glVertex3f((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ);
        GlStateManager.glEnd();

        GlStateManager.glBegin(GL11.GL_QUADS);
        GlStateManager.glVertex3f((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ);
        GlStateManager.glVertex3f((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ);
        GlStateManager.glVertex3f((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ);
        GlStateManager.glVertex3f((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ);
        GlStateManager.glEnd();

        GlStateManager.glBegin(GL11.GL_QUADS);
        GlStateManager.glVertex3f((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ);
        GlStateManager.glVertex3f((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ);
        GlStateManager.glVertex3f((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
        GlStateManager.glVertex3f((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
        GlStateManager.glEnd();

        GlStateManager.glBegin(GL11.GL_QUADS);
        GlStateManager.glVertex3f((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.minZ);
        GlStateManager.glVertex3f((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.minZ);
        GlStateManager.glVertex3f((float) boundingBox.minX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
        GlStateManager.glVertex3f((float) boundingBox.maxX, (float) boundingBox.maxY, (float) boundingBox.maxZ);
        GlStateManager.glEnd();

        GlStateManager.glBegin(GL11.GL_QUADS);
        GlStateManager.glVertex3f((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ);
        GlStateManager.glVertex3f((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ);
        GlStateManager.glVertex3f((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ);
        GlStateManager.glVertex3f((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ);
        GlStateManager.glEnd();

        GlStateManager.glBegin(GL11.GL_QUADS);
        GlStateManager.glVertex3f((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.minZ);
        GlStateManager.glVertex3f((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.minZ);
        GlStateManager.glVertex3f((float) boundingBox.minX, (float) boundingBox.minY, (float) boundingBox.maxZ);
        GlStateManager.glVertex3f((float) boundingBox.maxX, (float) boundingBox.minY, (float) boundingBox.maxZ);
        GlStateManager.glEnd();
    }

    public static void drawOutlinedBox(AxisAlignedBB bb) {
        GL11.glBegin(GL11.GL_LINES);
        {
            GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
            GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);

            GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
            GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);

            GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
            GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);

            GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
            GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);

            GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
            GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);

            GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
            GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);

            GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
            GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);

            GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
            GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);

            GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
            GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);

            GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
            GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);

            GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
            GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);

            GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
            GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
        }
        GL11.glEnd();
    }

    public static void drawESPOutline(AxisAlignedBB bb, float red, float green, float blue, float alpha, float width) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glLineWidth(width);
        GL11.glColor4f(red / 255f, green / 255f, blue / 255f, alpha / 255f);
        RenderUtil.drawOutlinedBox(bb);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }
    public static void drawESP(AxisAlignedBB bb, float red, float green, float blue, float alpha) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glColor4f(red / 255f, green / 255f, blue / 255f, alpha / 255f);
        RenderUtil.drawBox(bb);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }
    public static void drawBorderedRect(float x, float y, float width, float height, float lineSize, int color, int borderColor) {
        drawRect(x, y, width, height, color);
        drawRect(x, y, lineSize, height, borderColor);
        drawRect(x, y, width, lineSize, borderColor);
        drawRect(x + width - lineSize, y, lineSize, height, borderColor);
        drawRect(x, y + height - lineSize, width, lineSize, borderColor);

    }
    public static void drawRect(float x, float y, float w, float h, int color) {
        float lvt_5_2_;
        float p_drawRect_2_ = x + w;
        float p_drawRect_3_ = y + h;
        if (x < p_drawRect_2_) {
            lvt_5_2_ = x;
            x = p_drawRect_2_;
            p_drawRect_2_ = lvt_5_2_;
        }

        if (y < p_drawRect_3_) {
            lvt_5_2_ = y;
            y = p_drawRect_3_;
            p_drawRect_3_ = lvt_5_2_;
        }

        float lvt_5_3_ = (float)(color >> 24 & 255) / 255.0F;
        float lvt_6_1_ = (float)(color >> 16 & 255) / 255.0F;
        float lvt_7_1_ = (float)(color >> 8 & 255) / 255.0F;
        float lvt_8_1_ = (float)(color & 255) / 255.0F;
        Tessellator lvt_9_1_ = Tessellator.getInstance();
        BufferBuilder lvt_10_1_ = lvt_9_1_.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(lvt_6_1_, lvt_7_1_, lvt_8_1_, lvt_5_3_);
        lvt_10_1_.begin(7, DefaultVertexFormats.POSITION);
        lvt_10_1_.pos(x, p_drawRect_3_, 0.0D).endVertex();
        lvt_10_1_.pos(p_drawRect_2_, p_drawRect_3_, 0.0D).endVertex();
        lvt_10_1_.pos(p_drawRect_2_, y, 0.0D).endVertex();
        lvt_10_1_.pos(x, y, 0.0D).endVertex();
        lvt_9_1_.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static boolean isInViewFrustrum(Entity entity) {
        return isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck;
    }

    public static boolean isInViewFrustrum(AxisAlignedBB bb) {
        Entity current = Minecraft.getMinecraft().getRenderViewEntity();
        frustrum.setPosition(Objects.requireNonNull(current).posX, current.posY, current.posZ);
        return frustrum.isBoundingBoxInFrustum(bb);
    }

    public static ScaledResolution getResolution() {
        return new ScaledResolution(Minecraft.getMinecraft());
    }

    public static Vector3d project(double x, double y, double z) {
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);

        boolean result = GLU.gluProject((float) x, (float) y, (float) z, modelview, projection, viewport, modelview);
        if (result) {
            return new Vector3d(screenCoords.get(0), Display.getHeight() - screenCoords.get(1), screenCoords.get(2));
        }
        return null;
    }
    public static void renderTag(String name, double pX, double pY, double pZ, int color) {
        float scale = (float) (Minecraft.getMinecraft().player.getDistance(pX + ((IRenderManager) Minecraft.getMinecraft().getRenderManager()).getRenderPosX(), pY + ((IRenderManager) Minecraft.getMinecraft().getRenderManager()).getRenderPosY(), pZ + ((IRenderManager) Minecraft.getMinecraft().getRenderManager()).getRenderPosZ()) / 8.0D);
        if (scale < 1.6F) {
            scale = 1.6F;
        }
        scale /= 50;
        GL11.glPushMatrix();
        GL11.glTranslatef((float) pX, (float) pY + 1.4F, (float) pZ);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-scale, -scale, scale);
        GL11.glDisable(2896);
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);

        int width = Minecraft.getMinecraft().fontRenderer.getStringWidth(name) / 2;
        GL11.glPushMatrix();
        GL11.glPopMatrix();
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glScalef(0.5f,0.5f,0.5f);
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(name, -(width), -(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 7), color);
        GL11.glScalef(1f,1f,1f);
        GlStateManager.enableTexture2D();
        GL11.glDisable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(2929);
        GL11.glPopMatrix();
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }
}
