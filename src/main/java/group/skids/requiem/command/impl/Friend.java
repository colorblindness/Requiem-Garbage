package group.skids.requiem.command.impl;

import group.skids.requiem.client.Requiem;
import group.skids.requiem.command.Command;
import group.skids.requiem.utils.Printer;

public class Friend extends Command {

	public Friend() {
		super("Friend",new String[]{"friends","friend","f"});
	}

	@Override
	public void onRun(final String[] args) {
		if (args.length == 1) {
			Printer.print(".friend add <name>");
			return;
		}
		switch (args[1]) {
			case "add":
			case "a":
			case "Add":
			case "Ad":
			case "ad":
				if (args.length > 1) {
					if (Requiem.INSTANCE.getFriendManager().isFriend(args[2])) {
						Printer.print(args[2] + " is already your friend.");
						return;
					}
					if (args.length < 4) {
                        Printer.print("Added " + args[2] + " to your friends list without an alias.");
						Requiem.INSTANCE.getFriendManager().addFriend(args[2]);
					} else {
                        Printer.print("Added " + args[2] + " to your friends list with the alias " + args[3] + ".");
						Requiem.INSTANCE.getFriendManager().addFriendWithAlias(args[2], args[3]);
					}
				}
				break;
			case "del":
			case "delete":
			case "d":
			case "rem":
			case "remove":
			case "r":
				if (args.length > 1) {
					if (!Requiem.INSTANCE.getFriendManager().isFriend(args[2])) {
						Printer.print(args[2] + " is not your friend.");
						return;
					}
					if (Requiem.INSTANCE.getFriendManager().isFriend(args[2])) {
						Printer.print("Removed " + args[2] + " from your friends list.");
						Requiem.INSTANCE.getFriendManager().removeFriend(args[2]);
					}
				}
				break;
			case "c":
			case "clear":
				if (Requiem.INSTANCE.getFriendManager().getFriends().isEmpty()) {
					Printer.print("Your friends list is already empty.");
					return;
				}
				Printer.print("Your have cleared your friends list. Friends removed: " + Requiem.INSTANCE.getFriendManager().getFriends().size());
				Requiem.INSTANCE.getFriendManager().clearFriends();
				break;
			case "list":
			case "l":
				if (Requiem.INSTANCE.getFriendManager().getFriends().isEmpty()) {
					Printer.print("Your friends list is empty.");
					return;
				}
				Printer.print("Your current friends are: ");
				Requiem.INSTANCE.getFriendManager().getFriends().forEach(friend -> {
					Printer.print("Username: " + friend.getName() + (friend.getAlias() != null ? (" - Alias: " + friend.getAlias()) : ""));
				});
				break;
		}
	}
}
