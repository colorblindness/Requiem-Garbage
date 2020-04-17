package group.skids.requiem.module.impl.other;

import group.skids.requiem.events.UpdateEvent;
import group.skids.requiem.mixin.accessors.IEntity;
import group.skids.requiem.module.Module;
import net.b0at.api.event.Subscribe;

public class PortalChat extends Module {
    public PortalChat() {
        super("PortalChat", Category.VISUALS, 0x9D9798);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        ((IEntity) getMc().player).setInPortal(false);
    }
}
