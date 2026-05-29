package org.lozin.lilislottery.command.reload;

import org.bukkit.command.CommandSender;
import org.lozin.lilislottery.lottery.cache.RecipeCache;
import org.lozin.lilislottery.main.LilisLottery;
import org.lozin.lilislottery.ui.UiService;
import org.lozin.tool.string.Message;
import org.lozin.tool.yaml.YamlService;

public class CmdMainReload {
	public static boolean handle(CommandSender sender, String[] args){
		if (!check(args)) return false;
		try {
			long base = System.currentTimeMillis();
			LilisLottery.getInstance().reloadConfig();
			YamlService.clearFac();
			UiService.shutdownAll();
			RecipeCache.reload();
			Message.colorize(sender, "&a[DONE] &f重载完成 &7(" + (System.currentTimeMillis() - base) + "ms)");
		} catch (Exception e) {
			Message.colorize(sender, "&c[WARN] &f重载失败");
			return false;
		}
		return true;
	}
	private static boolean check(String[] args){
		return args.length == 1 && args[0].equalsIgnoreCase("reload");
	}
}
