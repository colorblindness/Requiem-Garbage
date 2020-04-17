package group.skids.requiem.module.impl.combat;


import group.skids.requiem.events.PacketEvent;
import group.skids.requiem.events.UpdateEvent;
import group.skids.requiem.mixin.accessors.ISPacketEntityVelocity;
import group.skids.requiem.mixin.accessors.ISPacketExplosion;
import group.skids.requiem.module.Module;
import group.skids.requiem.utils.value.impl.EnumValue;
import group.skids.requiem.utils.value.impl.NumberValue;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;

public class NoVelocity extends Module {
    private final NumberValue<Integer> horizontalModifier = new NumberValue<>("Horizontal", 0, 0, 100, 1);
    private final NumberValue<Integer> verticalModifier = new NumberValue<>("Vertical", 0, 0, 100, 1);
    private final EnumValue<Mode> mode = new EnumValue<>("Mode", Mode.NORMAL);

    public NoVelocity() {
        super("NoVelocity", Category.COMBAT, 0xff505050);

    }
    private enum Mode {
        NORMAL, AAC, DEV
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        final int vertical = verticalModifier.getValue();
        final int horizontal = horizontalModifier.getValue();
        if (getMc().player == null || getMc().world == null) return;
        if (mode.getValue() == Mode.NORMAL) {
            if (event.getType() == EventType.POST) {
                if (event.getPacket() instanceof SPacketEntityVelocity) {
                    final ISPacketEntityVelocity ipacket = (ISPacketEntityVelocity) event.getPacket();
                    final SPacketEntityVelocity packet = (SPacketEntityVelocity) event.getPacket();
                    if (packet.getEntityID() != getMc().player.getEntityId()) return;
                    if (vertical != 0 || horizontal != 0) {
                        ipacket.setMotionX(horizontal * packet.getMotionX() / 100);
                        ipacket.setMotionY(vertical * packet.getMotionY() / 100);
                        ipacket.setMotionZ(horizontal * packet.getMotionZ() / 100);
                    } else event.setCancelled(true);
                }
                if (event.getPacket() instanceof SPacketExplosion) {
                    final ISPacketExplosion ipacket = (ISPacketExplosion) event.getPacket();
                    final SPacketExplosion packet = (SPacketExplosion) event.getPacket();
                    if (vertical != 0 || horizontal != 0) {
                        ipacket.setPosX(horizontal * packet.getX() / 100);
                        ipacket.setPosY(vertical * packet.getY() / 100);
                        ipacket.setPosZ(horizontal * packet.getZ() / 100);
                    } else event.setCancelled(true);
                }
            }
        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (getMc().world == null || getMc().player == null) return;
        switch (mode.getValue()) {
            case AAC:
                if (event.getEventType() == EventType.PRE) {
                    if (getMc().player.hurtResistantTime > 13 && getMc().player.hurtResistantTime < 20 && !getMc().player.onGround) {
                        if (getMc().player.hurtResistantTime == 19) {
                            getMc().player.motionX *= 0.85;
                            getMc().player.motionZ *= 0.85;
                        }
                        else {
                            getMc().player.onGround = true;
                        }
                    }
                }
                break;
            case DEV:
                if (getMc().player.hurtResistantTime == 15) {
                    getMc().player.motionY *= 0.999;
                    getMc().player.onGround = true;
                }
                break;
            default:break;
        }
    }
}
