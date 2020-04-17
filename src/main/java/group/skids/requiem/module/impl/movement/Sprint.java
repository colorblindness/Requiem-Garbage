package group.skids.requiem.module.impl.movement;

import group.skids.requiem.events.UpdateEvent;
import group.skids.requiem.module.Module;
import net.b0at.api.event.Subscribe;

public class Sprint extends Module {
    public Sprint() {
        super("Sprint", Module.Category.MOVEMENT, 0xff00ff00);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (getMc().world == null || getMc().player == null) return;
        getMc().player.setSprinting(canSprint());
    }

    private boolean canSprint() {
        return getMc().player.getFoodStats().getFoodLevel() > 7 && getMc().gameSettings.keyBindForward.isKeyDown();
    }
}
