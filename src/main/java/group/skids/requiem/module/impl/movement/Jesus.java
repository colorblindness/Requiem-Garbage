package group.skids.requiem.module.impl.movement;

import group.skids.requiem.events.BoundingBoxEvent;
import group.skids.requiem.events.PacketEvent;
import group.skids.requiem.events.UpdateEvent;
import group.skids.requiem.mixin.accessors.ICPacketPlayer;
import group.skids.requiem.module.Module;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class Jesus extends Module {
    private final AxisAlignedBB WATER_WALK_AA = new AxisAlignedBB(0.D, 0.D, 0.D, 1.D, 0.99D, 1.D);
    public Jesus() {
        super("Jesus", Category.MOVEMENT, 0xff0000ff);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (getMc().world == null || getMc().player == null) return;
        if (event.getEventType() == EventType.POST || (getMc().player.isBurning() && isOnWater())) return;
        if (isInLiquid() && !getMc().gameSettings.keyBindSneak.isKeyDown() && !getMc().gameSettings.keyBindJump.isKeyDown() && getMc().player.fallDistance < 3)
            getMc().player.motionY = 0.1;
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (getMc().world == null || getMc().player == null) return;
        if (event.getType() == EventType.PRE) {
            if (getMc().player != null) {
                if (!(event.getPacket() instanceof CPacketPlayer) || isInLiquid() || !isOnLiquid() || getMc().player.isSneaking() || getMc().player.fallDistance > 3 || (getMc().player.isBurning() && isOnWater()))
                    return;
                ICPacketPlayer packet = (ICPacketPlayer) event.getPacket();
                if (getMc().player.isSprinting() && getMc().player.isInLava() && isOnLiquid())
                    getMc().player.connection.sendPacket(new CPacketEntityAction(getMc().player, CPacketEntityAction.Action.START_SPRINTING));
                getMc().player.connection.sendPacket(new CPacketEntityAction(getMc().player, CPacketEntityAction.Action.STOP_SPRINTING));
                packet.setY(packet.getY() + (getMc().player.ticksExisted % 2 == 0 ? 0.000000000002000111 : 0));
                packet.setOnGround(getMc().player.ticksExisted % 2 != 0);
            }
        }
    }

    @Subscribe
    public void onAddCollisionBoxToList(BoundingBoxEvent event) {
        if (getMc().world == null || getMc().player == null) return;
        if (getMc().player != null) {
            if (getMc().world == null || getMc().player.fallDistance > 3 || (getMc().player.isBurning() && isOnWater()))
                return;
            Block block = getMc().world.getBlockState(event.getPos()).getBlock();
            if (!isOnLiquid() || !(block instanceof BlockLiquid) || isInLiquid() || getMc().player.isSneaking())
                return;
            event.getCollidingBoxes().add(new AxisAlignedBB(0, 0, 0, 1, 1, 1).contract(0, 0.000000000002000111, 0).offset(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()));
        }
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

    private boolean isOnWater() {
        final double y = getMc().player.posY - 0.03;
        for (int x = MathHelper.floor(getMc().player.posX); x < MathHelper.ceil(getMc().player.posX); ++x) {
            for (int z = MathHelper.floor(getMc().player.posZ); z < MathHelper.ceil(getMc().player.posZ); ++z) {
                final BlockPos pos = new BlockPos(x, MathHelper.floor(y), z);
                if (getMc().world.getBlockState(pos).getBlock() instanceof BlockLiquid && getMc().world.getBlockState(pos).getBlock() == Blocks.WATER) {
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
