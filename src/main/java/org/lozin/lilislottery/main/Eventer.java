package org.lozin.lilislottery.main;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.lozin.lilislottery.event.UiClose;
import org.lozin.lilislottery.event.UiHandler;
import org.lozin.lilislottery.ui.UiFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Eventer implements Listener {
	private static Map<ItemStack, Integer> leakedItems = new ConcurrentHashMap<>();
	@EventHandler
	private void onUiClick(InventoryClickEvent event){
		Player player = (Player) event.getWhoClicked();
		UiHandler uiHandler = new UiHandler(UiFactory.getReg().get(player.getUniqueId()), event);
		if (!uiHandler.isSafe()) return;
		uiHandler.call();
		uiHandler.clickEvent();
		uiHandler.takeOffReplacer();
		if (uiHandler.isCancelled()) event.setCancelled(true);
	}
	@EventHandler
	private void onUiClose(InventoryCloseEvent event){
		Player player = (Player) event.getPlayer();
		UiClose uiClose = new UiClose(UiFactory.getReg().get(player.getUniqueId()), event);
		uiClose.call();
		UiFactory.getReg().remove(player.getUniqueId());
	}
}
