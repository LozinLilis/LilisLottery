package org.lozin.lilislottery.lottery.info.recipe;

import org.lozin.lilislottery.lottery.recipe.Recipe;
import org.lozin.lilislottery.lottery.recipe.Reward;
import org.lozin.tool.string.RegexService;

import java.util.*;
import java.util.stream.Collectors;

public class RewardInfo {
	private final List<Reward> rewards;
	public RewardInfo(Recipe recipe){
		this.rewards = recipe.getRewards();
	}
	public List<String> toLore(){
		return toLore(rewards);
	}
	public static List<String> toLore(List<Reward> rewards) {
		if (rewards == null || rewards.isEmpty()) return null;
		LinkedList<String> lore = new LinkedList<>();
		int totalWeight = 0;
		LinkedHashMap<Reward, Integer> mapper = new LinkedHashMap<>();
		for (Reward reward : rewards) {
			totalWeight += reward.getWeight();
			mapper.put(reward, reward.getWeight());
		}
		mapper = mapper.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
		for (Reward reward : mapper.keySet()) {
			String keyName = reward.getKeyName();
			String desc = reward.getDesc() != null ? reward.getDesc() : "/";
			int weight = reward.getWeight();
			lore.add("&e - &f"+keyName+" &7("+desc+") &6"+ RegexService.toDecimal((float) weight / totalWeight * 100) +"%");
		}
		return lore;
	}
}
