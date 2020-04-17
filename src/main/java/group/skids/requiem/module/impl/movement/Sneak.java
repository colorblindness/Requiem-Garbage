package group.skids.requiem.module.impl.movement;


import group.skids.requiem.events.PacketEvent;
import group.skids.requiem.events.UpdateEvent;
import group.skids.requiem.mixin.accessors.IEntityPlayerSP;
import group.skids.requiem.module.Module;
import group.skids.requiem.utils.value.impl.EnumValue;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.network.play.client.CPacketEntityAction;

import java.awt.*;

public class Sneak extends Module {
    private final EnumValue<mode> Mode = new EnumValue<>("Mode", mode.VANILLA);
    private boolean snuck = false;
    public Sneak() {
        super("Sneak", Category.MOVEMENT, new Color(0, 255, 0).getRGB());
    }

    public enum mode {
        VANILLA, NCP
    }

    @Override
    public void onDisable() {
        if (getMc().player == null || getMc().world == null) return;
        getMc().player.connection.sendPacket(new CPacketEntityAction(getMc().player, CPacketEntityAction.Action.STOP_SNEAKING));
    }

    @Override
    public void onEnable() {
        if (getMc().player == null || getMc().world == null) return;
        if (Mode.getValue() == mode.VANILLA) {
            getMc().player.connection.sendPacket(new CPacketEntityAction(getMc().player, CPacketEntityAction.Action.START_SNEAKING));
        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (getMc().world == null || getMc().player == null) return;
        if (Mode.getValue() == mode.NCP) {
            switch(event.getEventType()) {
                case PRE:
                    if (!((IEntityPlayerSP)getMc().player).isMoving()) return;
                     getMc().player.connection.sendPacket(new CPacketEntityAction(getMc().player, CPacketEntityAction.Action.START_SNEAKING));
                     getMc().player.connection.sendPacket(new CPacketEntityAction(getMc().player, CPacketEntityAction.Action.STOP_SNEAKING));
                    break;
                case POST:
                     getMc().player.connection.sendPacket(new CPacketEntityAction(getMc().player, CPacketEntityAction.Action.STOP_SNEAKING));
                     getMc().player.connection.sendPacket(new CPacketEntityAction(getMc().player, CPacketEntityAction.Action.START_SNEAKING));
                    break;
            }
        }
    }
}
