package group.skids.requiem.gui.impl;

import group.skids.requiem.gui.api.Button;
import group.skids.requiem.module.Module;
import group.skids.requiem.utils.Plane;
import group.skids.requiem.utils.RenderUtil;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ModuleButton extends Button {

    private Module module;

    public ModuleButton(Plane plane, Module module) {
        super(plane, module.getLabel());
        this.module = module;
    }

    @Override
    public void render() {
        final Color backColor = new Color(module.getColor());
        final Color moduleColor = new Color(backColor.getRed(), backColor.getGreen(), backColor.getBlue(), this.module.isEnabled() ? 255 : 75);
        RenderUtil.drawBorderedRect((float) this.plane.getX(), (float) this.plane.getY(), (float) this.plane.getWidth(), (float) this.plane.getHeight(), 1, moduleColor.getRGB(), Color.BLACK.getRGB());
        GL11.glScaled(textScale, textScale, 0);
        this.font.drawStringWithShadow(this.module.getLabel(), (float) ((this.plane.getX() + 2) / textScale), (float) ((this.plane.getY() + 5) / textScale), this.module.isEnabled() ? Color.WHITE.getRGB() : Color.GRAY.getRGB());
        GL11.glScaled(1 / textScale, 1 / textScale, 0);

    }

    @Override
    public void onClick() {
        this.module.toggle();
    }
}