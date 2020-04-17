package group.skids.requiem.mixin.impl;

import group.skids.requiem.client.Requiem;
import group.skids.requiem.events.GuiInitEvent;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreen.class)
public class MixinGuiScreen {


    @Inject(method = "initGui",at = @At("HEAD"))
    public void initGui(CallbackInfo ci) {
        Requiem.INSTANCE.getBus().fireEvent(new GuiInitEvent());
    }
}
