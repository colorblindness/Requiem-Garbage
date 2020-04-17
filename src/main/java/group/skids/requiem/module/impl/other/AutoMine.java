package group.skids.requiem.module.impl.other;

import group.skids.requiem.events.ClickBlockEvent;
import group.skids.requiem.events.UpdateEvent;
import group.skids.requiem.mixin.accessors.IPlayerControllerMP;
import group.skids.requiem.module.Module;
import net.b0at.api.event.Subscribe;
import net.b0at.api.event.types.EventType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class AutoMine extends Module {
    private BlockPos pos;

    public AutoMine() {
        super("AutoMine", Category.OTHER, new Color(0x9D9798).getRGB());
        setRenderLabel("Auto Mine");
    }

    @Subscribe
    public void onClickBlock(ClickBlockEvent event) {
        pos = event.getPos();
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (getMc().player == null || pos == null) return;
        if (event.getEventType() == EventType.PRE) {
            if (getMc().player.getDistanceSq(pos) > 25) {
                getMc().playerController.onPlayerDamageBlock(new BlockPos(0, 0, 0), EnumFacing.UP);
                return;
            }
            final float[] rotations = getBlockRotations(pos.getX(), pos.getY(), pos.getZ());
            event.setYaw(rotations[0]);
            event.setPitch(rotations[1]);
        } else {
            if (pos == null) {
                return;
            }
            if (getMc().player.getDistanceSq(pos) > 25) {
                getMc().playerController.onPlayerDamageBlock(new BlockPos(0, 0, 0), EnumFacing.UP);
                return;
            }
            ((IPlayerControllerMP) getMc().playerController).setBlockHitDelay(0);
            final ItemStack currentItem = getMc().player.inventory.getCurrentItem();
            int oldDamage = 0;
            if (!currentItem.isEmpty()) {
                oldDamage = currentItem.getItemDamage();
            }
            getMc().playerController.onPlayerDamageBlock(pos, EnumFacing.UP);
            getMc().player.swingArm(EnumHand.MAIN_HAND);
            if (!currentItem.isEmpty()) {
                currentItem.setItemDamage(oldDamage);
            }
        }
    }

    @Override
    public void onDisable() {
        if (getMc().world == null || getMc().player == null) return;
        pos = null;
        getMc().playerController.resetBlockRemoving();
    }

    private float[] getBlockRotations(final double x, final double y, final double z) {
        final double var4 = x - getMc().player.posX + 0.5;
        final double var5 = z - getMc().player.posZ + 0.5;
        final double var6 = y - (getMc().player.posY + getMc().player.getEyeHeight() - 1.0);
        final double var7 = MathHelper.sqrt(var4 * var4 + var5 * var5);
        final float var8 = (float) (Math.atan2(var5, var4) * 180.0 / 3.141592653589793) - 90.0f;
        return new float[]{var8, (float) (-(Math.atan2(var6, var7) * 180.0 / 3.141592653589793))};
    }
}
