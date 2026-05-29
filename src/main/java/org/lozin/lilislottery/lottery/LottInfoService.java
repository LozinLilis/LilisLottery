package org.lozin.lilislottery.lottery;

import lombok.NonNull;
import org.lozin.lilislottery.lottery.cache.RecipeCache;
import org.lozin.lilislottery.lottery.info.recipe.RequiredItemInfo;
import org.lozin.lilislottery.lottery.info.recipe.RewardInfo;
import org.lozin.lilislottery.lottery.recipe.Recipe;
import org.lozin.lilislottery.lottery.recipe.Reward;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface LottInfoService {
	static List<String> getReqLore(String id){
		if (!RecipeCache.getRecipes().containsKey(id)) return new ArrayList<>();
		Recipe recipe = RecipeCache.getById(id);
		if (recipe == null) return new ArrayList<>();
		return new RequiredItemInfo(recipe).toLore();
	}
	static List<String> getRewardLore(String id){
		if (!RecipeCache.getRecipes().containsKey(id)) return new ArrayList<>();
		Recipe recipe = RecipeCache.getById(id);
		if (recipe == null) return new ArrayList<>();
		return new RewardInfo(recipe).toLore();
	}
	static List<String> getRewardLore(@NonNull List<Reward> rewards){
		return RewardInfo.toLore(rewards).stream().map(s -> s.replaceAll("&(.)", "§$1")).collect(Collectors.toList());
	}
}
