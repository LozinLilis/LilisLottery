package org.lozin.lilislottery.lottery.recipe;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.lozin.lilislottery.lottery.cache.RecipeCache;
import org.lozin.lilislottery.ui.UiFactory;
import org.lozin.lilislottery.utils.item.nbt.NbtService;
import org.lozin.tool.math.WeightRand;
import org.lozin.tool.sound.Sounds;
import org.lozin.tool.string.Logger;
import org.lozin.tool.string.Message;
import org.lozin.tool.yaml.YamlService;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter @Setter
public class Recipe {
	private List<RequiredItem> requiredItems;
	private List<Lucky> lucky;
	private List<Reward> rewards;
	private String id;
	private String pathInFolder;
	
	public Recipe(String pathInFolder){
		this.pathInFolder = pathInFolder;
		this.id = YamlService.getString(pathInFolder, "id");
		if (this.id == null) {
			Logger.log("&c[ERROR] &f无法加载 &9" + pathInFolder + " &f中的配方 &c(未设置 id)");
			return;
		}
		this.requiredItems = formReq();
		this.rewards = formReward();
		this.lucky = formLuckys();
		if (RecipeCache.getRecipes().containsKey(this.id) || RecipeCache.getMultiRecipeId().contains(this.id)) {
			if (!RecipeCache.getMultiRecipeId().contains(this.id)) {
				RecipeCache.getMultiRecipeId().add(this.id);
			}
			Logger.log("&c[ERROR] &f无法加载 &9" + pathInFolder + " &f中的配方 &c(id已存在: "+id+")");
			return;
		}
		RecipeCache.getRecipes().put(this.id, this);
		Logger.log("&f[&a+&f] &f已注册配方: &e"+this.id+" &8("+this.pathInFolder+ ")");
	}
	public List<RequiredItem> formReq(){
		List<RequiredItem> result = new ArrayList<>();
		List<?> list = YamlService.getList(pathInFolder, "required_item");
		if (list == null || list.isEmpty()) {
			Logger.log("&c[WARN] &f文件: &e"+pathInFolder+" &f的 required_item 未正确设置");
			return result;
		}
		for (int index = 0; index < list.size(); index++) {
			String inner_id = YamlService.getString(pathInFolder, "required_item."+index+".inner_id");
			String name = YamlService.getString(pathInFolder, "required_item."+index+".name");
			int amount = YamlService.getNum(pathInFolder, "required_item."+index+".amount").intValue();
			if (inner_id == null || name == null || amount <= 0) {
				Logger.log("&c[WARN] &f文件: &e"+pathInFolder+" &f的 required_item."+index+" 未正确设置");
				continue;
			}
			result.add(new RequiredItem(inner_id, amount, name));
		}
		return result;
	}
	public List<Reward> formReward() {
		List<Reward> result = new ArrayList<>();
		Map<?,?> map = YamlService.getMap(pathInFolder, "reward");
		if (map == null) {
			Logger.log("&e[INFO] &f未找到 &e"+id+" &f设置中的&b reward &f参数");
			return result;
		}
		for (Object key : map.keySet()) {
			String keyName = key.toString();
			Map<?,?> content = YamlService.getMap(pathInFolder, "reward." + keyName);
			if (content == null) {
				Logger.log("&e[INFO] &f未找到配方 &e"+id+" &f设置中的&b reward." + keyName + " &f参数");
				continue;
			}
			String desc = content.get("desc") == null ? null : content.get("desc").toString();
			int weight = content.get("weight") == null ? 0 : Integer.parseInt(content.get("weight").toString());
			List<String> commands = content.get("command") == null ? null : (List<String>) content.get("command");
			if (commands == null) desc = "无奖励";
			Reward reward = new Reward(keyName, commands, weight, desc);
			result.add(reward);
		}
		return result;
	}
	public Reward chooseReward(List<Reward> rewards){
		Map<Reward, Integer> mapper = new WeakHashMap<>();
		for (Reward reward : rewards) {
			mapper.put(reward, reward.getWeight());
		}
		return WeightRand.choose(mapper);
	}
	private List<Lucky> formLuckys(){
		List<Lucky> result = new ArrayList<>();
		Map<?,?> map = YamlService.getMap(pathInFolder, "lucky");
		if (map == null) {
			Logger.log("&e[INFO] &f未找到配方 &e"+id+" &f设置中的&b lucky &f参数");
			return result;
		}
		for (Object key : map.keySet()) {
			String innerId = key.toString();
			Integer amount = YamlService.getNum(pathInFolder, "lucky." + innerId + ".amount") == null ? 1 : YamlService.getNum(pathInFolder, "lucky." + innerId + ".amount").intValue();
			List<String> formulas = (List<String>) YamlService.getList(pathInFolder, "lucky." + innerId + ".formula");
			if (formulas == null) {
				Logger.log("&e[INFO] &f未找到配方 &e"+id+" &f设置中的&b lucky." + innerId + ".formula &f参数");
				continue;
			}
			result.add(new Lucky(this, innerId, amount, formulas));
		}
		return result;
	}
	public List<Reward> reformRewards(Lucky lucky) {
		if (lucky == null) return rewards;
		List<String> formulas = lucky.getFormulas();
		if (formulas == null || formulas.isEmpty()) return rewards;
		Map<Reward, String> mapper = new HashMap<>();
		rewards.forEach(reward -> mapper.put(reward, reward.getWeight().toString()));
		Pattern pattern = Pattern.compile("^(.+)\\s*:\\s*(.*)");
		for (String formula : formulas){
			Matcher matcher = pattern.matcher(formula);
			if (matcher.matches()) {
				String key = matcher.group(1);
				String tf = matcher.group(2);
				Reward reward = Reward.getByKeyName(rewards, key);
				if (reward != null) mapper.put(reward, tf);
			}
		}
		if (! mapper.isEmpty()) {
			List<Reward> newRewards = new ArrayList<>();
			ScriptEngineManager m = new ScriptEngineManager();
			ScriptEngine engine = m.getEngineByName("js");
			for (Reward reward : rewards) {
				engine.put(reward.getKeyName(), reward.getWeight());
			}
			for (Reward reward : mapper.keySet()) {
				String formula = mapper.get(reward);
				try {
					int weight = Math.round(Math.max(Float.parseFloat(engine.eval(formula).toString()), 0.0f));
					Reward r = new Reward(reward.getKeyName(), reward.getCommands(), weight, reward.getDesc());
					newRewards.add(r);
				} catch (ScriptException e) {
					e.printStackTrace();
				}
			}
			return newRewards;
		}
		return rewards;
	}
	//
	public void costLuckyItem(UiFactory ui, int slot, Player player) {
		Inventory inventory = ui.getInventory();
		List<Lucky> luckys = this.lucky;
		ItemStack itemStack = inventory.getItem(slot);
		if (itemStack == null) return;
		String innerId = NbtService.getCompound(itemStack, Lucky.getDetectKey());
		if (lucky == null) return;
		Lucky lucky = Lucky.getById(this, innerId);
		if (lucky == null) return;
		int req = lucky.getAmount();
		int amount = itemStack.getAmount();
		if (amount >= req) {
			itemStack.setAmount(amount-req);
			inventory.setItem(slot, itemStack);
			if (amount-req < req) {
				if (amount-req > 0) {
					Message.colorize(player, "&e[WARN] &f该加成物数量不足, 已自动取下");
					Sounds.play(player, Sounds.warn);
				}
				else {
					Message.colorize(player, "&e[WARN] &f该加成物已消耗殆尽");
					Sounds.play(player, Sounds.warn);
				}
				if (player.getInventory().firstEmpty() == -1){
					Message.colorize(player, "&c[WARN] &f你的背包已满, 已将物品丢到地上");
					Sounds.play(player, Sounds.warn);
					player.getWorld().dropItem(player.getLocation(), itemStack);
				}
				else player.getInventory().addItem(itemStack);
				ui.reformLuckyItem();
				ui.setReplaced(false);
			}
		}
	}
}
