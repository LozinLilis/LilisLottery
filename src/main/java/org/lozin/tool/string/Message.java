package org.lozin.tool.string;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Message {
	public static void colorize(CommandSender sender, String str) {
		str = str.replaceAll("&", "§");
		sender.sendMessage(str);
	}
	public static void colorize(CommandSender sender, String... str) {
		for (String s : str) {
			colorize(sender, s);
		}
	}
	public static void colorize(Player sender, List<String> str) {
		for (String s : str) {
			colorize(sender, s);
		}
	}
}
