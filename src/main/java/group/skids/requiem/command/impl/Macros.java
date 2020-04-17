package group.skids.requiem.command.impl;

import group.skids.requiem.client.Requiem;
import group.skids.requiem.command.Command;
import group.skids.requiem.utils.Printer;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class Macros extends Command {

    public Macros() {
        super("Macros", new String[]{"mac","macro","macros"});
    }

    @Override
    public void onRun(String[] args) {
        if (args.length > 1) {
            switch (args[1].toLowerCase()) {
                case "list":
                    if (Requiem.INSTANCE.getMacroManager().getMacros().isEmpty()) {
                        Printer.print("Your macro list is empty.");
                        return;
                    }
                    Printer.print("Your macros are:");
                    Requiem.INSTANCE.getMacroManager().getMacros().values().forEach(macro -> Printer.print("Label: " + macro.getLabel() + ", Keybind: " + Keyboard.getKeyName(macro.getKey()) + ", Text: " + macro.getText() + "."));
                    break;
                case "reload":
                    Requiem.INSTANCE.getMacroManager().clearMacros();
                    Requiem.INSTANCE.getMacroManager().load();
                    Printer.print("Reloaded macros.");
                    break;
                case "remove":
                case "delete":
                    if (args.length < 3) {
                        Printer.print("Invalid args.");
                        return;
                    }
                    if (Requiem.INSTANCE.getMacroManager().isMacro(args[2])) {
                        Requiem.INSTANCE.getMacroManager().removeMacroByLabel(args[2]);
                        Printer.print("Removed a macro named " + args[2] + ".");
                        if (Requiem.INSTANCE.getMacroManager().getMacroFile().exists()) {
                            Requiem.INSTANCE.getMacroManager().save();
                        } else {
                            try {
                                Requiem.INSTANCE.getMacroManager().getMacroFile().createNewFile();
                                Requiem.INSTANCE.getMacroManager().save();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Printer.print(args[2] + " is not a macro.");
                    }
                    break;
                case "clear":
                    if (Requiem.INSTANCE.getMacroManager().getMacros().isEmpty()) {
                        Printer.print("Your macro list is empty.");
                        return;
                    }
                    Printer.print("Cleared all macros.");
                    Requiem.INSTANCE.getMacroManager().clearMacros();
                    if (Requiem.INSTANCE.getMacroManager().getMacroFile().exists()) {
                        Requiem.INSTANCE.getMacroManager().save();
                    } else {
                        try {
                            Requiem.INSTANCE.getMacroManager().getMacroFile().createNewFile();
                            Requiem.INSTANCE.getMacroManager().save();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case "add":
                case "create":
                    if (args.length < 5) {
                        Printer.print("Invalid args.");
                        return;
                    }
                    int keyCode = Keyboard.getKeyIndex(args[3].toUpperCase());
                    if (keyCode != -1 && !Keyboard.getKeyName(keyCode).equals("NONE")) {
                        if (Requiem.INSTANCE.getMacroManager().getMacroByKey(keyCode) != null) {
                            Printer.print("There is already a macro bound to that key.");
                            return;
                        }
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 4; i < args.length; i++) {
                            stringBuilder.append(args[i]);
                            if (i != args.length - 1) stringBuilder.append(" ");
                        }
                        Requiem.INSTANCE.getMacroManager().addMacro(args[2], keyCode, stringBuilder.toString());
                        Printer.print("Bound a macro named " + args[2] + " to the key " + Keyboard.getKeyName(keyCode) + ".");
                        if (Requiem.INSTANCE.getMacroManager().getMacroFile().exists()) {
                            Requiem.INSTANCE.getMacroManager().save();
                        } else {
                            try {
                                Requiem.INSTANCE.getMacroManager().getMacroFile().createNewFile();
                                Requiem.INSTANCE.getMacroManager().save();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Printer.print("That is not a valid key code.");
                    }
                    break;
            }
        } else Printer.print("Not enough arguments!");
    }
}