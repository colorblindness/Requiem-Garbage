package group.skids.requiem.command.impl;

import group.skids.requiem.client.Requiem;
import group.skids.requiem.command.Command;
import group.skids.requiem.utils.Printer;

public class ClientTitle extends Command {

    public ClientTitle() {
        super("ClientTitle",new String[]{"clienttitle","ct","title","ctitle","clientlabel","label","clientname","name"});
    }

    @Override
    public void onRun(String[] args) {
        if (args.length > 1) {
            final StringBuilder stringBuilder = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                stringBuilder.append(args[i]);
                if (i != args.length - 1) stringBuilder.append(" ");
            }
            Requiem.INSTANCE.getLabel().setValue(stringBuilder.toString());
            Printer.print("Set client name to " + Requiem.INSTANCE.getLabel().getValue() + "!");
        }
    }
}
