package group.skids.requiem.mixin.impl;

import group.skids.requiem.client.Requiem;
import group.skids.requiem.events.EntityChunkEvent;
import net.b0at.api.event.types.EventType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldClient.class)
public class MixinWorldClient {

    @Inject(method = "onEntityAdded", at = @At("HEAD"))
    private void onEntityAdded(Entity entity, CallbackInfo info) {
        if (Minecraft.getMinecraft().world != null && Minecraft.getMinecraft().player != null) {
            if (entity instanceof EntityPlayer)
                Requiem.INSTANCE.getBus().fireEvent(new EntityChunkEvent(EventType.PRE, entity));
        }
    }

    @Inject(method = "onEntityRemoved", at = @At("HEAD"))
    private void onEntityRemoved(Entity entity, CallbackInfo info) {
        if (Minecraft.getMinecraft().world != null && Minecraft.getMinecraft().player != null) {
            if (entity instanceof EntityPlayer)
                Requiem.INSTANCE.getBus().fireEvent(new EntityChunkEvent(EventType.POST, entity));
        }
    }
}