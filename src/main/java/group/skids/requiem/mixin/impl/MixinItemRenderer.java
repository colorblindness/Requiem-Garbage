package group.skids.requiem.mixin.impl;

import group.skids.requiem.mixin.accessors.IItemRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer implements IItemRenderer {
    @Accessor(value = "equippedProgressOffHand")
    public abstract void setEquippedProgressOffHand(float progressOffHand);
}
