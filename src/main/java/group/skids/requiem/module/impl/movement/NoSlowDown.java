package group.skids.requiem.module.impl.movement;

import group.skids.requiem.client.Requiem;
import group.skids.requiem.events.PacketEvent;
import group.skids.requiem.events.UpdateEvent;
import group.skids.requiem.events.UpdateInputEvent;
import group.skids.requiem.mixin.accessors.ICPacketPlayer;
import group.skids.requiem.module.Module;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.network.play.client.CPacketPlayer;

public class NoSlowDown extends Module {
    public NoSlowDown() {
        super("NoSlowDown", Category.MOVEMENT, 0xff0a3f0E);
    }


    @Subscribe
    public void onInputUpdate(UpdateInputEvent event) {
        if (getMc().player.isHandActive() && !getMc().player.isRiding()) {
            getMc().player.movementInput.moveStrafe *= 5;
            getMc().player.movementInput.moveForward *= 5;
        }
    }

    @Subscribe
    public void onPre(PacketEvent event) {
        if (event.getType() == EventType.PRE && event.getPacket() instanceof CPacketPlayer && getMc().player.isActiveItemStackBlocking() && getMc().player.onGround && !Requiem.INSTANCE.getModuleManager().getModule("speed").isEnabled() && (getMc().player.moveStrafing != 0 || getMc().player.moveForward != 0) && !getMc().gameSettings.keyBindJump.isKeyDown()) {
            getMc().player.motionY = 0.02;
            ((ICPacketPlayer)event.getPacket()).setY(((ICPacketPlayer)event.getPacket()).getY() + 0.3);
        }
    }
}