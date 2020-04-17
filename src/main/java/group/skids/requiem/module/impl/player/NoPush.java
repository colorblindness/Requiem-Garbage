package group.skids.requiem.module.impl.player;

import group.skids.requiem.events.UpdateEvent;
import group.skids.requiem.module.Module;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.network.play.client.CPacketEntityAction;

public class NoPush extends Module {
    private float savedReduction;

    public NoPush() {
        super("NoPush", Category.PLAYER, 0xff0030f4);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (getMc().world == null || getMc().player == null) return;
        getMc().player.entityCollisionReduction = 1.0F;
    }

    @Override
    public void onEnable() {
        if (getMc().world == null || getMc().player == null) return;
        savedReduction = getMc().player != null ? getMc().player.entityCollisionReduction : 0.0f;
    }

    @Override
    public void onDisable() {
        if (getMc().world == null || getMc().player == null) return;
        getMc().player.entityCollisionReduction = savedReduction;
    }
}
