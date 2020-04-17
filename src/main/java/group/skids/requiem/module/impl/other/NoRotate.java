package group.skids.requiem.module.impl.other;

import group.skids.requiem.events.PacketEvent;
import group.skids.requiem.mixin.accessors.ISPacketPosLook;
import group.skids.requiem.module.Module;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

import java.awt.Color;

public class NoRotate extends Module {
    public NoRotate() {
        super("NoRotate", Category.OTHER, 0x9D9798);
        setRenderLabel("No Rotate");
        setDescription("Cancel ncp rotation flags.");
    }
    
    @Subscribe
    public void handle(PacketEvent event) {
        if (getMc().world == null || getMc().player == null) return;
        if (event.getType() == EventType.POST && event.getPacket() instanceof SPacketPlayerPosLook) {
            ISPacketPosLook packet = (ISPacketPosLook) event.getPacket();
            if (getMc().player != null && getMc().world != null && getMc().player.rotationYaw != -180 && getMc().player.rotationPitch != 0) {
                packet.setYaw(getMc().player.rotationYaw);
                packet.setPitch( getMc().player.rotationPitch);
            }
        }
    }
}
