package group.skids.requiem.command.impl;


import group.skids.requiem.client.Requiem;
import group.skids.requiem.command.Command;
import group.skids.requiem.utils.Printer;
import org.apache.commons.lang3.text.WordUtils;

public class Help extends Command {

	public Help() {
		super("Help", new String[]{"h", "help"});
	}

	@Override
	public void onRun(final String[] s) {
		Requiem.INSTANCE.getCommandManager().getCommandMap().values().forEach(command -> {
			Printer.print(WordUtils.capitalizeFully(command.getLabel()));
		});
	}
}
