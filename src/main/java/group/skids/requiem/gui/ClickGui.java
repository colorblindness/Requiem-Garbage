package group.skids.requiem.gui;

import group.skids.requiem.client.Requiem;
import group.skids.requiem.gui.api.Button;
import group.skids.requiem.gui.impl.CategoryButton;
import group.skids.requiem.gui.impl.ModuleButton;
import group.skids.requiem.module.Module;
import group.skids.requiem.utils.Plane;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;

public class ClickGui extends GuiScreen {

    private FontRenderer font = Minecraft.getMinecraft().fontRenderer;

    private ArrayList<CategoryButton> categoryButtons = new ArrayList<>();

    public void init() {
        int offsetX = 50;

        for (Module.Category category : Module.Category.values()) {
            final ArrayList<Module> modules = Requiem.INSTANCE.getModuleManager().getModulesInCategory(category);
            int width = 0;
            for (Module module : modules) {
                int labelWidth = (int) (font.getStringWidth(module.getLabel()) * Button.textScale) + 4;
                if (labelWidth > width) width = labelWidth;
            }
            CategoryButton button = new CategoryButton(
                    new Plane(offsetX, 50, width, (font.FONT_HEIGHT * Button.textScale) + 10, true),
                    category.name(),
                    modules
            );
            this.categoryButtons.add(button);
            offsetX += width + 10;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        for (CategoryButton categoryButton : this.categoryButtons) {
            categoryButton.render();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            for (CategoryButton button : this.categoryButtons) {
                if (button.isHovered(mouseX, mouseY)) {
                    button.onClick();
                    return;
                } else {
                    for (ModuleButton moduleButton : button.getModuleButtons()) {
                        if (moduleButton.isHovered(mouseX, mouseY)) {
                            moduleButton.onClick();
                        }
                    }
                }
            }

        }
    }

}