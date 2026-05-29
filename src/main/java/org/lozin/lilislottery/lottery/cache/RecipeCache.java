package org.lozin.lilislottery.lottery.cache;

import lombok.Getter;
import org.lozin.lilislottery.lottery.recipe.Recipe;
import org.lozin.lilislottery.main.LilisLottery;
import org.lozin.tool.string.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RecipeCache {
	@Getter private static final Map<String, Recipe> recipes = new ConcurrentHashMap<>();
	@Getter private static List<String> multiRecipeId = new ArrayList<>();
	public static Recipe getById(String id) {
		if (recipes.containsKey(id)) return recipes.get(id);
		return null;
	}
	public static void reg(String pathInFolder) {
		Recipe recipe = new Recipe(pathInFolder);
	}
	public static void regAll() {
		File folder = new File(LilisLottery.getInstance().getDataFolder(), "Recipes");
		File[] files = folder.listFiles();
		if (files == null || files.length == 0) {
			Logger.log("&c[ERROR] &f配方文件夹下未找到任何配方文件");
			return;
		}
		for (File file : files) {
			String pathInFolder = "Recipes" + File.separator + file.getName();
			new Recipe(pathInFolder);
		}
		for (String id : multiRecipeId){
			recipes.remove(id);
			Logger.log("&c[ERROR] &f配方id &c" + id + " &f存在多个, 已禁用");
		}
	}
	public static void reload() {
		recipes.clear();
		multiRecipeId.clear();
		regAll();
	}
}
