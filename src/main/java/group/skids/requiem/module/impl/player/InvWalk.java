package group.skids.requiem.module.impl.player;

import group.skids.requiem.events.GuiInitEvent;
import group.skids.requiem.events.UpdateEvent;
import group.skids.requiem.module.Module;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.input.Keyboard;

public class InvWalk extends Module {
    private final KeyBinding[] MOVEMENT_KEYS = new KeyBinding[]{getMc().gameSettings.keyBindForward, getMc().gameSettings.keyBindRight, getMc().gameSettings.keyBindBack, getMc().gameSettings.keyBindLeft, getMc().gameSettings.keyBindJump, getMc().gameSettings.keyBindSprint};

    public InvWalk() {
        super("InvWalk", Category.PLAYER, 0xff3300ff);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (getMc().world == null || getMc().player == null) return;
        if (event.getEventType() == EventType.PRE) {
            if (getMc().currentScreen != null && !(getMc().currentScreen instanceof GuiChat)) {

                getMc().player.rotationYaw += Keyboard.isKeyDown(Keyboard.KEY_RIGHT) ? 4 : Keyboard.isKeyDown(Keyboard.KEY_LEFT) ? -4 : 0;

                getMc().player.rotationPitch += (Keyboard.isKeyDown(Keyboard.KEY_DOWN) ? 4 : Keyboard.isKeyDown(Keyboard.KEY_UP) ? -4 : 0) * 0.75;

                getMc().player.rotationPitch = MathHelper.clamp(getMc().player.rotationPitch, -90, 90);

                runCheck();
            }
        }
    }

    @Subscribe
    public void onInit(GuiInitEvent event) {
        if (getMc().currentScreen != null && !(getMc().currentScreen instanceof GuiChat)) {
            runCheck();
        }
    }

    private void runCheck() {
        for (KeyBinding keyBinding : MOVEMENT_KEYS) {
            if (Keyboard.isKeyDown(keyBinding.getKeyCode())) {
                if (keyBinding.getKeyConflictContext() != KeyConflictContext.UNIVERSAL) {
                    keyBinding.setKeyConflictContext(KeyConflictContext.UNIVERSAL);
                }
                KeyBinding.setKeyBindState(keyBinding.getKeyCode(), true);
            } else {
                KeyBinding.setKeyBindState(keyBinding.getKeyCode(), false);
            }
        }
    }
}
