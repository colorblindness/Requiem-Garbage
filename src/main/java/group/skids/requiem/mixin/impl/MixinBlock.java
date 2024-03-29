package group.skids.requiem.mixin.impl;
import group.skids.requiem.client.Requiem;
import group.skids.requiem.events.BoundingBoxEvent;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;


@Mixin(Block.class)
public abstract class MixinBlock {

    private BoundingBoxEvent bbEvent;


    @Inject(
            method = "addCollisionBoxToList(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/entity/Entity;Z)V",
            at = @At("HEAD")
    )
    private void addCollisionBox(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState, CallbackInfo ci) {
        synchronized (this) {
            Block block = (Block) (Object) (this);
            bbEvent = new BoundingBoxEvent(block, pos, block.getCollisionBoundingBox(state, world, pos), collidingBoxes, entity);
            Requiem.INSTANCE.getBus().fireEvent(bbEvent);
        }
    }

    @Redirect(
            method = "addCollisionBoxToList(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/entity/Entity;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/state/IBlockState;getCollisionBoundingBox(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/math/AxisAlignedBB;"
            )
    )
    private AxisAlignedBB getBB(IBlockState state, IBlockAccess world, BlockPos pos) {
        synchronized (this) {
            AxisAlignedBB bb = (bbEvent == null) ?
                    state.getCollisionBoundingBox(world, pos) :
                    bbEvent.getBoundingBox();
            bbEvent = null;
            return bb;
        }
    }
}