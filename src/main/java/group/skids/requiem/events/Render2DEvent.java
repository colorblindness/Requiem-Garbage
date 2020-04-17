package group.skids.requiem.events;

import net.b0at.api.event.Event;
import net.minecraft.client.gui.ScaledResolution;

public class Render2DEvent extends Event {
    private final float partialTicks;
    private final ScaledResolution scaledResolution;

    public Render2DEvent(float partialTicks, final ScaledResolution scaledResolution) {
        this.partialTicks = partialTicks;
        this.scaledResolution = scaledResolution;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public ScaledResolution getScaledResolution() {
        return scaledResolution;
    }
}