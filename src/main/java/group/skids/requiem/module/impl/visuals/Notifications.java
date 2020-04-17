package group.skids.requiem.module.impl.visuals;

import group.skids.requiem.client.Requiem;
import group.skids.requiem.events.EntityChunkEvent;
import group.skids.requiem.events.Render2DEvent;
import group.skids.requiem.module.Module;
import group.skids.requiem.utils.Printer;
import net.b0at.api.event.Subscribe;
import net.minecraft.entity.player.EntityPlayer;

public class Notifications extends Module {
    public Notifications() {
        super("Notifications", Category.VISUALS, 0xff6ff666);
    }

    @Subscribe
    public void onEntityEnterChunk(EntityChunkEvent event) {
        if (getMc().world == null || getMc().player == null) return;
        switch (event.getType()) {
            case PRE:
                if (event.getEntity() instanceof EntityPlayer && !event.getEntity().getName().equals(getMc().player.getName())) {
                    Printer.print(event.getEntity().getName() + " has entered your view distance!");
                }
                break;
            case POST:
                if (event.getEntity() instanceof EntityPlayer && !event.getEntity().getName().equals(getMc().player.getName())) {
                    Printer.print(event.getEntity().getName() + " has left your view distance!");
                }
                break;
        }
    }
}
