package group.skids.requiem.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;

public class InventoryUtils {
    public static int getSlotOfItem(Item input) {
        for (int i = 0; i < 36; i++) {
            Item item = Minecraft.getMinecraft().player.inventory.getStackInSlot(i).getItem();
            if (item == input) return i < 9 ? i + 36 : i;
        }
        return -1;
    }
}
