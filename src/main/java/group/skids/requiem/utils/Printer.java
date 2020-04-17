package group.skids.requiem.utils;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

public class Printer {

    public static void print(String message) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(String.format(ChatFormatting.LIGHT_PURPLE + "[%s]"+ ChatFormatting.WHITE + " %s", "Requiem", message)));
    }
}