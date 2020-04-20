package group.skids.requiem.gui.api;

import group.skids.requiem.utils.Plane;
import group.skids.requiem.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

public class Button {

    protected Minecraft mc = Minecraft.getMinecraft();

    /**
     * I declared this here so it COULD be changed, doesn't mean it should
     * Minecraft fontrenderer is still on top
     */
    protected FontRenderer font = mc.fontRenderer;

    public static double textScale = 1.2;

    //This can be deleted just don't forget to
    protected int height = this.font.FONT_HEIGHT + 2;

    protected String message;

    protected Plane plane;

    public Button(Plane plane, String message) {
        this.plane = plane;
        this.message = message;
    }

    public void render() {
        RenderUtil.drawBorderedRect((float) this.plane.getX(), (float) this.plane.getY(), (float) this.plane.getWidth(), (float) this.plane.getHeight(), 1, -1, 0xff50ff50);
        GL11.glScaled(textScale, textScale,0);
        this.font.drawStringWithShadow(this.message, (float) ((this.plane.getX() + 2) / textScale), (float) ((this.plane.getY() + 2) / textScale), -1);
        GL11.glScaled(1 / textScale, 1 / textScale, 0);
    }

    /**
     * runs stuff to do on click :D
     */
    public void onClick() {

    }

    public Plane getPlane() {
        return this.plane;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean isHovered(final int mouseX, final int mouseY) {
        return mouseX >= this.plane.getX() && mouseX <= this.plane.getX() + this.plane.getWidth() && mouseY >= this.plane.getY() && mouseY <= this.plane.getY() + this.plane.getHeight();
    }
}
