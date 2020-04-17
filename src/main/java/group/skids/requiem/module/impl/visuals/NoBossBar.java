package group.skids.requiem.module.impl.visuals;

import group.skids.requiem.events.RenderBossBarEvent;
import group.skids.requiem.module.Module;
import net.b0at.api.event.Subscribe;

public class NoBossBar extends Module {

    public NoBossBar() {
        super("NoBossBar", Category.VISUALS, 0xff666666);
    }

    @Subscribe
    public void renderBossBar(RenderBossBarEvent event) {
        event.setCancelled(true);
    }
}
