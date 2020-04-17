package group.skids.requiem.module.impl.combat;

import group.skids.requiem.events.UpdateEvent;
import group.skids.requiem.module.Module;
import group.skids.requiem.utils.InventoryUtils;
import group.skids.requiem.utils.value.impl.BooleanValue;
import net.b0at.api.event.Subscribe;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;

public class AutoTotem extends Module {

    private final BooleanValue gui = new BooleanValue("GUI", false);

    public AutoTotem() {
        super("AutoTotem", Category.COMBAT, 0xff660000);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        Item offhand = getMc().player.getHeldItemOffhand().getItem();
        if (offhand != Items.TOTEM_OF_UNDYING) {
            int slot = InventoryUtils.getSlotOfItem(Items.TOTEM_OF_UNDYING);
            if (slot != -1) {
                if (getMc().currentScreen != null && !this.gui.getValue()) return;
                getMc().playerController.windowClick(getMc().player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, getMc().player);
                getMc().playerController.windowClick(getMc().player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, getMc().player);
                getMc().playerController.windowClick(getMc().player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, getMc().player);
                getMc().playerController.updateController();

            }
        }
    }
}
