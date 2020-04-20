package group.skids.requiem.module.impl.visuals;

import group.skids.requiem.client.Requiem;
import group.skids.requiem.module.Module;

public class ClickGUI extends Module {
    public ClickGUI() {
        super("ClickGUI", Category.VISUALS, -1);
    }

    @Override
    public void onEnable() {
        if (getMc().world == null || getMc().player == null) return;
        getMc().displayGuiScreen(Requiem.INSTANCE.getClickGui());
        this.toggle();
    }
}
