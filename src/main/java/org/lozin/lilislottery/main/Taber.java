package org.lozin.lilislottery.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.lozin.lilislottery.tab.gui.TabOpenLott;
import org.lozin.lilislottery.tab.reload.TabMain;

import java.util.List;

public class Taber implements TabCompleter {
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("lilis_lot")) {
			if (TabMain.checker(args)) return TabMain.handle(args);
			if (TabOpenLott.checker(args)) return TabOpenLott.handle(args);
		}
		return null;
	}
}
