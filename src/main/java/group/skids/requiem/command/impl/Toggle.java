package group.skids.requiem.command.impl;

import group.skids.requiem.client.Requiem;
import group.skids.requiem.command.Command;
import group.skids.requiem.module.Module;
import group.skids.requiem.utils.Printer;

public class Toggle extends Command {

    public Toggle() {
        super("Toggle", new String[]{"t", "toggle"});
    }

    @Override
    public void onRun(final String[] s) {
        if (s.length <= 1) {
            Printer.print("Not enough args.");
            return;
        }
        if (!Requiem.INSTANCE.getModuleManager().isModule(s[1])) {
        	Printer.print("Invalid module!");
		}
        for (Module m : Requiem.INSTANCE.getModuleManager().getModuleMap().values()) {
            if (m.getLabel().toLowerCase().equals(s[1])) {
                m.toggle();
                Printer.print("Toggled " + m.getLabel());
                break;
            }
        }
    }
}
