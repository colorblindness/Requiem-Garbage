package group.skids.requiem.module.impl.visuals;

import group.skids.requiem.events.HurtCamEvent;
import group.skids.requiem.module.Module;
import net.b0at.api.event.Subscribe;

public class NoHurtCam extends Module {
    public NoHurtCam() {
        super("NoHurtCam", Category.VISUALS, 0xff666666);
    }

    @Subscribe
    public void hurtCam(HurtCamEvent event) {
        event.setCancelled(true);
    }
}
