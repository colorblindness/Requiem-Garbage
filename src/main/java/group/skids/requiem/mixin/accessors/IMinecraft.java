package group.skids.requiem.mixin.accessors;

import net.minecraft.util.Session;

public interface IMinecraft {

    void setSession(Session session);

    void setRightClickDelayTimer(int delay);
}
