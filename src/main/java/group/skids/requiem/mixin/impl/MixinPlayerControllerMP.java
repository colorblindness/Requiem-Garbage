package group.skids.requiem.mixin.impl;

import group.skids.requiem.client.Requiem;
import group.skids.requiem.events.ClickBlockEvent;
import group.skids.requiem.mixin.accessors.IPlayerControllerMP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP implements IPlayerControllerMP {

    @Accessor
    @Override
    public abstract void setBlockHitDelay(int blockHitDelay);

    @Inject(method = "clickBlock", at = @At("HEAD"))
    private void clickBlock(BlockPos loc, EnumFacing face, CallbackInfoReturnable<Boolean> cir) {
        Requiem.INSTANCE.getBus().fireEvent(new ClickBlockEvent(loc, face));
    }
}