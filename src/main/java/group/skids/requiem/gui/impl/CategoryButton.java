package group.skids.requiem.gui.impl;

import group.skids.requiem.gui.api.Button;
import group.skids.requiem.module.Module;
import group.skids.requiem.utils.Plane;
import group.skids.requiem.utils.RenderUtil;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class CategoryButton extends Button {

    /**
     * We define this elsewhere, we get the modules in the category
     */
    private ArrayList<Module> modules;

    /**
     * the buttons!
     */
    private ArrayList<ModuleButton> moduleButtons = new ArrayList<>();

    public boolean extended;

    public CategoryButton(Plane plane, String message, ArrayList<Module> modules) {
        super(plane, message);
        this.modules = modules;
    }

    @Override
    public void render() {
        RenderUtil.drawBorderedRect((float) this.plane.getX(), (float) this.plane.getY(), (float) this.plane.getWidth(), (float) this.plane.getHeight(), 1, Color.BLACK.getRGB(), Color.CYAN.getRGB());
        GL11.glScaled(textScale, textScale, 0);
        this.font.drawString(this.message, (int) ((this.plane.getX() + 2) / textScale), (int) ((this.plane.getY() + 5) / textScale), -1);
        GL11.glScaled(1 / textScale, 1 / textScale, 0);
        //the +2 here adds a little distance between the category label and the modules
        int y = (int) (this.plane.getY() + 2 + this.plane.getHeight());

        for (Module module : this.modules) {
            ModuleButton button = new ModuleButton(new Plane(this.plane.getX(), y, this.plane.getWidth(), this.plane.getHeight(), true), module);
            button.render();

            // To add settings: scan for the module's settings and create buttons for them here
            // Increment the y value with them or it will cause other issues
            this.moduleButtons.add(button);
            y += this.plane.getHeight();
        }

    }

    @Override
    public void onClick() {
        this.extended = !this.extended;
    }

    public ArrayList<ModuleButton> getModuleButtons() {
        return this.moduleButtons;
    }
}
