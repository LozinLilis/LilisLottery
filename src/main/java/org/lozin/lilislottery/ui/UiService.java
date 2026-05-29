package org.lozin.lilislottery.ui;

import org.bukkit.entity.Player;

public class UiService {
	public static void shutdown(Player player){
		if (UiFactory.getReg().containsKey(player.getUniqueId())) {
			player.closeInventory();
			UiFactory.getReg().remove(player.getUniqueId());
		}
	}
	public static void shutdownAll(){
		for (UiFactory factory : UiFactory.getReg().values()) {
			factory.getWatcher().closeInventory();
		}
		UiFactory.getReg().clear();
	}
}
