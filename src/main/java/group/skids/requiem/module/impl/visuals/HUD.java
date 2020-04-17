package group.skids.requiem.module.impl.visuals;

import group.skids.requiem.client.Requiem;
import group.skids.requiem.events.Render2DEvent;
import group.skids.requiem.module.Module;
import net.b0at.api.event.Subscribe;

import java.util.ArrayList;
import java.util.Comparator;

public class HUD extends Module {
    public HUD() {
        super("HUD", Category.VISUALS, 0xff666666);
    }

    @Subscribe
    public void onRender2D(Render2DEvent event) {
        if (getMc().world == null || getMc().player == null) return;
        if (getMc().gameSettings.showDebugInfo) return;
        getMc().fontRenderer.drawStringWithShadow(Requiem.INSTANCE.getLabel().getValueAsString() + " " + Requiem.INSTANCE.getVersion(), 2, 2, 0xff9500ff);
        int offsetY = 2;
        final ArrayList<Module> sorted = new ArrayList<>(Requiem.INSTANCE.getModuleManager().getModuleMap().values());
        sorted.sort(Comparator.comparingDouble(module->-getMc().fontRenderer.getStringWidth((module.getRenderLabel() != null ? module.getRenderLabel() : module.getLabel()) + (module.getSuffix() != null ? " " + module.getSuffix() : ""))));
        for (Module module : sorted) {
            if (!module.isEnabled() || module.isHidden()) continue;
            final String renderString = (module.getRenderLabel() != null ? module.getRenderLabel() : module.getLabel()) + (module.getSuffix() != null ? " " + module.getSuffix() : "");
            getMc().fontRenderer.drawStringWithShadow(renderString, event.getScaledResolution().getScaledWidth() - 2 - getMc().fontRenderer.getStringWidth(renderString), offsetY, module.getColor());
            offsetY += 2 + getMc().fontRenderer.FONT_HEIGHT;
        }
    }
}
