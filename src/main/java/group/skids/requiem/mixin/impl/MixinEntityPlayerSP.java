package group.skids.requiem.mixin.impl;


import group.skids.requiem.client.Requiem;
import group.skids.requiem.events.MotionEvent;
import group.skids.requiem.events.PushEvent;
import group.skids.requiem.events.UpdateEvent;
import group.skids.requiem.events.UpdateInputEvent;
import group.skids.requiem.mixin.accessors.IEntityPlayerSP;
import net.b0at.api.event.types.EventType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends MixinEntityPlayer implements IEntityPlayerSP {
    @Shadow
    protected Minecraft mc;

    private UpdateEvent eventUpdate;

    @Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/Tutorial;handleMovement(Lnet/minecraft/util/MovementInput;)V"))
    private void onLivingUpdate(CallbackInfo ci) {
        Requiem.INSTANCE.getBus().fireEvent(new UpdateInputEvent());
    }

    @Override
    public void move(MoverType p_move_1_, double p_move_2_, double p_move_4_, double p_move_6_) {
        MotionEvent event = new MotionEvent(p_move_2_, p_move_4_,p_move_6_);
        Requiem.INSTANCE.getBus().fireEvent(event);
        super.move(p_move_1_, event.getX(), event.getY(), event.getZ());
    }

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    private void onPushOutOfBlocks(double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        final PushEvent eventPushOutOfBlocks = new PushEvent();
        Requiem.INSTANCE.getBus().fireEvent(eventPushOutOfBlocks);
        if (eventPushOutOfBlocks.isCancelled()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"))
    private void onUpdateWalkingPlayerHead(CallbackInfo ci) {
        eventUpdate = new UpdateEvent(getRotationYaw(),getRotationPitch(), getPosY(), isOnGround(), EventType.PRE);
        Requiem.INSTANCE.getBus().fireEvent(eventUpdate);
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/util/math/AxisAlignedBB;minY:D"))
    private double onUpdateWalkingPlayerMinY(AxisAlignedBB boundingBox) {
        return eventUpdate.getY();
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;onGround:Z"))
    private boolean onUpdateWalkingPlayerOnGround(EntityPlayerSP player) {
        return eventUpdate.isOnGround();
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;rotationYaw:F"))
    private float onUpdateWalkingPlayerRotationYaw(EntityPlayerSP player) {
        return eventUpdate.getYaw();
    }

    @Redirect(method = "onUpdateWalkingPlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/EntityPlayerSP;rotationPitch:F"))
    private float onUpdateWalkingPlayerRotationPitch(EntityPlayerSP player) {
        return eventUpdate.getPitch();
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"))
    private void onUpdateWalkingPlayerReturn(CallbackInfo ci) {
        Requiem.INSTANCE.getBus().fireEvent(new UpdateEvent(getRotationYaw(),getRotationPitch(), getPosY(), isOnGround(), EventType.POST));
    }

    @Override
    public boolean isMoving() {
        return mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown();
    }
}