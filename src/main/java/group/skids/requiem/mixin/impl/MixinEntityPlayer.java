package group.skids.requiem.mixin.impl;

import group.skids.requiem.client.Requiem;
import group.skids.requiem.events.InsideBlockRenderEvent;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends MixinEntity {

    @Inject(method = "isEntityInsideOpaqueBlock",at = @At("HEAD"),cancellable = true)
    private void onIsEntityInsideOpaqueBlock(CallbackInfoReturnable<Boolean> cir) {
        final InsideBlockRenderEvent insideBlockRenderEvent = new InsideBlockRenderEvent();
        Requiem.INSTANCE.getBus().fireEvent(insideBlockRenderEvent);
        if(insideBlockRenderEvent.isCancelled()) {
            cir.setReturnValue(false);
        }
    }
}