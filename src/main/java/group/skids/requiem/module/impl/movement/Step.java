package group.skids.requiem.module.impl.movement;

import group.skids.requiem.client.Requiem;
import group.skids.requiem.events.UpdateEvent;
import group.skids.requiem.module.Module;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;

public class Step extends Module {
    private final double[] oneblockPositions = {0.42D, 0.75D};
    private int packets;

    public Step() {
        super("Step", Category.MOVEMENT, 0xffff0039);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (getMc().world == null || getMc().player == null) return;
        if (event.getEventType() == EventType.POST && !Requiem.INSTANCE.getModuleManager().getModule("speed").isEnabled()) {
            if (getMc().player.isCollidedHorizontally && getMc().player.onGround && isStepable(getMc().player)) {
                this.packets++;
            }
            final AxisAlignedBB bb = getMc().player.getEntityBoundingBox();
            for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX + 1.0D); x++) {
                for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ + 1.0D); z++) {
                    final Block block = getMc().world.getBlockState(new BlockPos(x, bb.maxY + 1, z)).getBlock();
                    if (!(block instanceof BlockAir)) {
                        return;
                    }
                }
            }
            if (getMc().player.onGround && isStepable(getMc().player) && !getMc().player.isInsideOfMaterial(Material.WATER) && !getMc().player.isInsideOfMaterial(Material.LAVA) && getMc().player.fallDistance == 0 && !getMc().gameSettings.keyBindJump.isKeyDown() && getMc().player.isCollidedHorizontally && !getMc().player.isOnLadder() && this.packets > this.oneblockPositions.length - 2) {
                for (double position : this.oneblockPositions) {
                    getMc().player.connection.sendPacket(new CPacketPlayer.Position(getMc().player.posX, getMc().player.posY + position, getMc().player.posZ, true));
                }
                getMc().player.setPosition(getMc().player.posX, getMc().player.posY + this.oneblockPositions[this.oneblockPositions.length - 1], getMc().player.posZ);
                this.packets = 0;
            }
        }
    }

    private boolean isStepable(EntityPlayerSP player) {
        if (isOnLiquid() || isInLiquid()) return false;
        ArrayList<BlockPos> collisionBlocks = new ArrayList<>();
        BlockPos pos1 = new BlockPos(player.getEntityBoundingBox().minX - 0.001D, player.getEntityBoundingBox().minY - 0.001D, player.getEntityBoundingBox().minZ - 0.001D);
        BlockPos pos2 = new BlockPos(player.getEntityBoundingBox().maxX + 0.001D, player.getEntityBoundingBox().maxY + 0.001D, player.getEntityBoundingBox().maxZ + 0.001D);

        if (player.world.isAreaLoaded(pos1, pos2)) for (int x = pos1.getX(); x <= pos2.getX(); x++)
            for (int y = pos1.getY(); y <= pos2.getY() + 1; y++)
                for (int z = pos1.getZ(); z <= pos2.getZ(); z++)
                    if (y > player.posY - 1.0D && y <= player.posY) collisionBlocks.add(new BlockPos(x, y, z));

        for (BlockPos collisionBlock : collisionBlocks)
            if (getMc().world.getBlockState(collisionBlock.add(0, 0, 0)).getBlock() instanceof BlockFence ||getMc().world.getBlockState(collisionBlock.add(0, 0, 0)).getBlock() instanceof BlockWall || !(getMc().world.getBlockState(collisionBlock.add(0, 2, 0)).getBlock() instanceof BlockAir || getMc().world.getBlockState(collisionBlock.add(0, 1, 0)).getBlock() instanceof BlockSnow || getMc().world.getBlockState(collisionBlock.add(0, 2, 0)).getBlock() instanceof BlockFlower || getMc().world.getBlockState(collisionBlock.add(0, 2, 0)).getBlock() instanceof BlockGrass || getMc().world.getBlockState(collisionBlock.add(0, 2, 0)).getBlock() instanceof BlockTallGrass) || !(getMc().world.getBlockState(collisionBlock.add(0, 1, 0)).getBlock() instanceof BlockAir || getMc().world.getBlockState(collisionBlock.add(0, 1, 0)).getBlock() instanceof BlockFlower || getMc().world.getBlockState(collisionBlock.add(0, 1, 0)).getBlock() instanceof BlockGrass || getMc().world.getBlockState(collisionBlock.add(0, 1, 0)).getBlock() instanceof BlockTallGrass) || !player.isCollidedHorizontally || !player.onGround || !(player.movementInput.forwardKeyDown || player.movementInput.backKeyDown || player.movementInput.leftKeyDown || player.movementInput.rightKeyDown))
                return false;

        return true;
    }

    private boolean isOnLiquid() {
        final double y = getMc().player.posY - 0.03;
        for (int x = MathHelper.floor(getMc().player.posX); x < MathHelper.ceil(getMc().player.posX); ++x) {
            for (int z = MathHelper.floor(getMc().player.posZ); z < MathHelper.ceil(getMc().player.posZ); ++z) {
                final BlockPos pos = new BlockPos(x, MathHelper.floor(y), z);
                if (getMc().world.getBlockState(pos).getBlock() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInLiquid() {
        final double y = getMc().player.posY + 0.01;
        for (int x = MathHelper.floor(getMc().player.posX); x < MathHelper.ceil(getMc().player.posX); ++x) {
            for (int z = MathHelper.floor(getMc().player.posZ); z < MathHelper.ceil(getMc().player.posZ); ++z) {
                final BlockPos pos = new BlockPos(x, (int) y, z);
                if (getMc().world.getBlockState(pos).getBlock() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }
}