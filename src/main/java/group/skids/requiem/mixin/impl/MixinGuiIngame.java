package group.skids.requiem.mixin.impl;

import group.skids.requiem.client.Requiem;
import group.skids.requiem.events.RenderPotionEffectsEvent;
import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class MixinGuiIngame {

    @Inject(method = "renderPotionEffects", at = @At("HEAD"), cancellable = true)
    private void renderPotionEffects(CallbackInfo ci) {
        RenderPotionEffectsEvent event = new RenderPotionEffectsEvent();
        Requiem.INSTANCE.getBus().fireEvent(event);
        if (event.isCancelled()) ci.cancel();
    }


}
