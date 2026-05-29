package org.lozin.lilislottery.tab.gui;

import org.lozin.lilislottery.lottery.cache.RecipeCache;
import org.lozin.tool.string.TabUtils;

import java.util.ArrayList;
import java.util.List;

public class TabOpenLott {
	public static boolean checker(String[] args){
		return args.length == 2 && args[0].equals("open");
	}
	public static List<String> handle(String[] args){
		return TabUtils.getCompletion(new ArrayList<>(RecipeCache.getRecipes().keySet()), args[1]);
	}
}
