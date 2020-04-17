package group.skids.requiem.mixin.impl;

import group.skids.requiem.client.Requiem;
import group.skids.requiem.events.*;
import group.skids.requiem.mixin.accessors.IMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements IMinecraft {

    @Shadow
    public int displayWidth;
    @Shadow
    public int displayHeight;

    @Accessor
    @Override
    public abstract void setSession(Session session);

    @Accessor
    @Override
    public abstract void setRightClickDelayTimer(int delay);

    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        Requiem.INSTANCE.setupClient();
    }

    @Inject(method = "shutdownMinecraftApplet", at = @At("HEAD"))
    private void shutdownMinecraftApplet(CallbackInfo ci) {
        Requiem.INSTANCE.shutdownClient();
    }

    @Inject(method = "runTick", at = @At("HEAD"))
    private void onTick(CallbackInfo info) {
        Requiem.INSTANCE.getBus().fireEvent(new TickEvent());
    }


    @Inject(method = "runTickKeyboard", at = @At(value = "INVOKE", remap = false, target = "Lorg/lwjgl/input/Keyboard;getEventKey()I", ordinal = 0, shift = At.Shift.BEFORE))
    private void onKeyboard(CallbackInfo callbackInfo) {
        final int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
        if (Keyboard.getEventKeyState()) {
            Requiem.INSTANCE.getBus().fireEvent(new KeyPressedEvent(i));
        }
    }

    @Inject(method = "toggleFullscreen", at = @At("RETURN"))
    private void onToggleFullscreen(CallbackInfo info) {
        final FullScreenEvent event = new FullScreenEvent(this.displayWidth, this.displayHeight);
        Requiem.INSTANCE.getBus().fireEvent(event);
    }

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    private void onLoadWorld(@Nullable WorldClient worldClientIn, String loadingMessage, CallbackInfo callbackInfo) {
        final WorldLoadEvent eventWorldLoad = new WorldLoadEvent(worldClientIn);
        Requiem.INSTANCE.getBus().fireEvent(eventWorldLoad);
    }

    @Inject(method = "resize", at = @At("HEAD"))
    public void onResize(int width, int height, CallbackInfo callbackInfo) {
        ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        final ResizeEvent rsevent = new ResizeEvent(scaledresolution);
        Requiem.INSTANCE.getBus().fireEvent(rsevent);
    }
}