package group.skids.requiem.events;

import net.b0at.api.event.Event;
import net.minecraft.entity.Entity;


public class RenderNameEvent extends Event {
    private final Entity entity;
    public RenderNameEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}
