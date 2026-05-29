package org.lozin.lilislottery.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.lozin.lilislottery.ui.UiAction;
import org.lozin.lilislottery.ui.UiFactory;
import org.lozin.lilislottery.utils.item.ItemFactory;
import org.lozin.lilislottery.utils.item.nbt.NbtService;
import org.lozin.tool.sound.Sounds;
import org.lozin.tool.string.Message;

import java.util.Map;
import java.util.WeakHashMap;

public class UiClose extends Event {
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private boolean unsafe = false;
	@Override public HandlerList getHandlers() {
		return handlers;
	}
	
	private final UiFactory ui;
	private final InventoryCloseEvent event;
	private final Player player;
	private final Inventory inventory;
	private boolean isOnline;
	
	public UiClose(UiFactory ui, InventoryCloseEvent event) {
		this.ui = ui; this.player = (Player) event.getPlayer();
		if (ui == null || ui.getWatcher() != player) {
			unsafe = true;
		}
		this.event = event;
		this.inventory = event.getInventory();
		this.isOnline = player.isOnline();
	}
	public boolean call() {
		if (unsafe) return false;
		handle();
		return true;
	}
	public void handle() {
		itemBack();
	}
	private void itemBack() {
		Map<ItemStack, Integer> leakedItems = new WeakHashMap<>();
		for (ItemStack item : inventory.getContents()){
			if (! ItemFactory.valid(item)) continue;
			String action = NbtService.getCompound(item, UiAction.key());
			if (action == null) {
				if (player.getInventory().firstEmpty() == -1){
					if (isOnline) {
						Message.colorize(player, "&c[WARN] &f背包已满, 已将该物品丢出");
						player.getWorld().dropItem(player.getLocation(), item);
						Sounds.play(player, Sounds.warn);
					}
					else {
						if (leakedItems.containsKey(item)) leakedItems.put(item, leakedItems.get(item)+1);
						else leakedItems.put(item, 1);
					}
				}
				else {
					player.getInventory().addItem(item);
					Sounds.play(player, Sounds.take_off);
				}
			}
		}
		if (!leakedItems.isEmpty()) {
		}
	}
	public static void itemBack(Player player, Inventory inventory) {
		if (inventory == null) return;
		Map<ItemStack, Integer> leakedItems = new WeakHashMap<>();
		for (ItemStack item : inventory.getContents()){
			if (! ItemFactory.valid(item)) continue;
			String action = NbtService.getCompound(item, UiAction.key());
			if (action == null) {
				if (player.getInventory().firstEmpty() == -1){
					if (player.isOnline()){
						Message.colorize(player, "&c[WARN] &f背包已满, 已将该物品丢出");
						player.getWorld().dropItem(player.getLocation(), item);
						Sounds.play(player, Sounds.warn);
					}
					else {
						if (leakedItems.containsKey(item)) leakedItems.put(item, leakedItems.get(item)+1);
						else leakedItems.put(item, 1);
					}
				}
				else {
					player.getInventory().addItem(item);
					Sounds.play(player, Sounds.take_off);
				}
			}
		}
		if (!leakedItems.isEmpty()) {
		}
	}
}
