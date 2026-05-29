package org.lozin.lilislottery.lottery.info.recipe;

import org.lozin.lilislottery.lottery.recipe.Recipe;
import org.lozin.lilislottery.lottery.recipe.RequiredItem;

import java.util.LinkedList;
import java.util.List;

public class RequiredItemInfo {
	private final List<RequiredItem> requiredItems;
	public RequiredItemInfo(Recipe recipe) {
		this.requiredItems = recipe.getRequiredItems();
	}
	 public List<String> toLore(){
		if (requiredItems == null || requiredItems.isEmpty()) return null;
		LinkedList<String> lore = new LinkedList<>();
		for (RequiredItem item : requiredItems) {
			String name = item.getName();
			int amount = item.getAmount();
			if (amount > 0) lore.add(("&e - &7"+amount+"个 &f"+name.replaceAll("&.", "").replaceAll("§.", "")).replaceAll("&", "§"));
			else lore.add(("&e - &7拥有 &f"+name.replaceAll("&.", "").replaceAll("§.", "")+" &7(不消耗)").replaceAll("&", "§"));
		}
		return lore;
	}
}
