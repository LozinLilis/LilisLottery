package org.lozin.lilislottery.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.lozin.lilislottery.command.gui.OpenLott;
import org.lozin.lilislottery.command.reload.CmdMainReload;

public class Commander implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("lilis_lot")) {
			if (CmdMainReload.handle(sender, args)) return true;
			if (OpenLott.handle(sender, args)) return true;
		}
		return false;
	}
}
