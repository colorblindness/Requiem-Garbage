package group.skids.requiem.mixin.impl;


import group.skids.requiem.client.Requiem;
import group.skids.requiem.events.RenderNameEvent;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Render.class)
public class MixinRender {

    @Inject(method = "renderLivingLabel", at = @At("HEAD"), cancellable = true)
    private void renderLivingLabel(Entity entityIn, String str, double x, double y, double z, int maxDistance, CallbackInfo ci) {
        final RenderNameEvent event = new RenderNameEvent(entityIn);
        Requiem.INSTANCE.getBus().fireEvent(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}