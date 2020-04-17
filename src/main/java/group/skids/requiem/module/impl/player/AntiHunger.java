package group.skids.requiem.module.impl.player;

import group.skids.requiem.events.UpdateEvent;
import group.skids.requiem.module.Module;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.network.play.client.CPacketEntityAction;

public class AntiHunger extends Module {
    private boolean wasOnGround;

    public AntiHunger() {
        super("AntiHunger", Category.PLAYER, 0xff3300ff);
    }

    @Subscribe
    public void sendPacket(UpdateEvent event) {
        if (getMc().world == null || getMc().player == null) return;
        if (event.getEventType() == EventType.PRE) {
            if (getMc().player.isSprinting() && !getMc().player.onGround && getMc().gameSettings.keyBindJump.isKeyDown())
                getMc().player.connection.sendPacket(new CPacketEntityAction(getMc().player, CPacketEntityAction.Action.STOP_SPRINTING));
            if (!wasOnGround && getMc().player.onGround) {
                event.setOnGround(true);
                return;
            }
            if (getMc().playerController.getIsHittingBlock()) {
                return;
            }
            event.setOnGround(false);
        } else this.wasOnGround = getMc().player.onGround;
    }
}
