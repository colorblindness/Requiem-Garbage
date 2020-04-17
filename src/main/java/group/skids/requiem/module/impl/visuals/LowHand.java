package group.skids.requiem.module.impl.visuals;

import group.skids.requiem.events.UpdateEvent;
import group.skids.requiem.mixin.accessors.IItemRenderer;
import group.skids.requiem.module.Module;
import group.skids.requiem.utils.value.impl.NumberValue;
import net.b0at.api.event.Subscribe;

public class LowHand extends Module {

    private final NumberValue<Integer> height = new NumberValue<>("Height", 70, 0, 100, 1);

    public LowHand() {
        super("LowHand", Category.VISUALS, 0xff666666);
    }

    @Subscribe
    public void onUpdateEvent(UpdateEvent event) {
        ((IItemRenderer) getMc().entityRenderer.itemRenderer).setEquippedProgressOffHand(this.height.getValue() / 100f);
    }
}
