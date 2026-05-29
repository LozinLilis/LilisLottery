package org.lozin.lilislottery.utils.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.lozin.lilislottery.lottery.recipe.RequiredItem;
import org.lozin.tool.sound.Sounds;
import org.lozin.tool.string.Message;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class ItemCost {
	public static boolean cost(Player player, List<RequiredItem> reqs) {
		if (reqs == null || reqs.isEmpty()) return false;
		
		Map<String, Integer> required = new WeakHashMap<>();
		reqs.forEach(item -> {required.merge(item.getInnerId(), item.getAmount(), Integer::sum);});
		Inventory inventory = player.getInventory();
		
		Map<String, Integer> had = new WeakHashMap<>();
		for (String innerId : required.keySet()) had.put(innerId, 0);
		for (ItemStack item : inventory.getContents()){
			if (!ItemFactory.valid(item)) continue;
			String innerId = ItemFactory.getCostId(item);
			if (innerId == null || !required.containsKey(innerId)) continue;
			had.put(innerId, had.get(innerId) + item.getAmount());
		}
		
		Map<String, Integer> missed = new WeakHashMap<>();
		for (String innerId : had.keySet()){
			int requiredAmount = required.get(innerId);
			int hadAmount = had.get(innerId);
			if (hadAmount < requiredAmount) missed.put(innerId, requiredAmount-hadAmount);
		}
		
		if (!missed.isEmpty()){
			for (String innerId : missed.keySet()){
				Message.colorize(player, "&c[FAIL] &f缺少 &b" + missed.get(innerId) + " &f个 &e" + Objects.requireNonNull(RequiredItem.getNameById(reqs, innerId)).replaceAll("§.", "").replaceAll("&.", ""));
			}
			Sounds.play(player, Sounds.fail);
			return false;
		}
		
		for (int i = 0; i < inventory.getContents().length; i++){
			ItemStack item = inventory.getContents()[i];
			if (!ItemFactory.valid(item)) continue;
			String innerId = ItemFactory.getCostId(item);
			if (innerId == null || !required.containsKey(innerId)) continue;
			int currentAmount = item.getAmount();
			int need = required.get(innerId);
			int deduct = Math.min(currentAmount, need);
			if (deduct > 0){
				item.setAmount(currentAmount-deduct);
				required.put(innerId, need-deduct);
				if (item.getAmount() <= 0) inventory.clear(i);
			}
			if (required.get(innerId) <= 0) required.remove(innerId);
		}
		return true;
	}
}
