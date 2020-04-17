package group.skids.requiem.module.impl.visuals;

import group.skids.requiem.module.Module;

public class Fullbright extends Module {
    public Fullbright() {
        super("Fullbright", Category.VISUALS, 0xff600666);
    }

    @Override
    public void onEnable() {
        getMc().gameSettings.gammaSetting = 1000f;
    }

    @Override
    public void onDisable() {
        //if you don't want it bright, might as well make it dark
        getMc().gameSettings.gammaSetting = 0.5f;
    }
}
