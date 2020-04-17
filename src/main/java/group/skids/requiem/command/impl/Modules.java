package group.skids.requiem.command.impl;

import group.skids.requiem.client.Requiem;
import group.skids.requiem.command.Command;
import group.skids.requiem.utils.Printer;

public class Modules extends Command {

    public Modules() {
        super("Modules", new String[]{"modules","mods","m"});
    }

    @Override
    public void onRun(final String[] s) {
        StringBuilder mods = new StringBuilder("Modules (" + Requiem.INSTANCE.getModuleManager().getModuleMap().values().size() + "): ");
        Requiem.INSTANCE.getModuleManager().getModuleMap().values()
                .forEach(mod -> mods.append(mod.isEnabled() ? "\247a" : "\247c").append(mod.getLabel()).append("\247r, "));
        Printer.print(mods.toString().substring(0, mods.length() - 2));
    }
}