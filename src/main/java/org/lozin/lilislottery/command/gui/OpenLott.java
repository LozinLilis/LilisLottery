package org.lozin.lilislottery.command.gui;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.lozin.lilislottery.lottery.cache.RecipeCache;
import org.lozin.lilislottery.ui.UiFactory;

public class OpenLott {
	private static boolean check(String[] args) {
		return args.length == 2 && args[0].equalsIgnoreCase("open");
	}
	public static boolean handle(CommandSender sender, String[] args) {
		if (!check(args)) return false;
		String id = args[1];
		if (!(sender instanceof Player)) {
			sender.sendMessage("§c[WARN] §f该指令仅限玩家执行");
			return false;
		}
		if (! RecipeCache.getRecipes().containsKey(id)) {
			sender.sendMessage("§c[WARN] §f未找到该配方");
			return false;
		}
		Player player = (Player) sender;
		UiFactory ui = new UiFactory(player, args[1]);
		Inventory inventory = ui.getInventory();
		player.openInventory(inventory);
		ui.formItem();
		ui.insertItem();
		return true;
	}
}
