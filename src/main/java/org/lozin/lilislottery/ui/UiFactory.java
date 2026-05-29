package org.lozin.lilislottery.ui;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.lozin.lilislottery.Ymls;
import org.lozin.lilislottery.lottery.LottInfoService;
import org.lozin.lilislottery.main.LilisLottery;
import org.lozin.lilislottery.utils.item.ItemFactory;
import org.lozin.tool.string.Logger;
import org.lozin.tool.string.RegexService;
import org.lozin.tool.yaml.YamlService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter @Setter
public class UiFactory {
	private String name;
	private Player owner;
	private Inventory inventory;
	private Player watcher;
	private int size;
	private String id;
	private boolean replaced = false;
	private final Map<Integer, ItemStack> items = new ConcurrentHashMap<>();
	private final Map<String, List<Integer>> actions = new ConcurrentHashMap<>();
	@Getter private static final Map<UUID, UiFactory> reg = new ConcurrentHashMap<>();
	private int luckySlot;
	
	public UiFactory(Player owner) {
		this.watcher = owner;
		this.size = YamlService.getNum(Ymls.lotteryGUI.getPathInFolder(), "size").intValue();
		this.name = YamlService.getString(Ymls.lotteryGUI.getPathInFolder(), "title");
		String o = YamlService.getString(Ymls.lotteryGUI.getPathInFolder(), "owner");
		if (o == null || size <= 0 || name == null) {
			Logger.log("&c[ERROR] &fGUI的基础参数未正确设置");
			return;
		}
		if (o.equals("public")) this.owner = null;
		else this.owner = owner;
		this.inventory = Bukkit.createInventory(this.owner, this.size, this.name.replace("&", "§"));
		reg.put(owner.getUniqueId(), this);
	}
	/**
	 * 用于Recipe的方式
	 * **/
	public UiFactory(Player owner, String id){
		this.watcher = owner;
		this.id = id;
		this.size = YamlService.getNum(Ymls.lotteryGUI.getPathInFolder(), "size").intValue();
		this.name = YamlService.getString(Ymls.lotteryGUI.getPathInFolder(), "title");
		String o = YamlService.getString(Ymls.lotteryGUI.getPathInFolder(), "owner");
		if (o == null || size <= 0 || name == null) {
			Logger.log("&c[ERROR] &fGUI的基础参数未正确设置");
			return;
		}
		if (o.equals("public")) this.owner = null;
		else this.owner = owner;
		this.inventory = Bukkit.createInventory(this.owner, this.size, this.name.replace("&", "§"));
		reg.put(owner.getUniqueId(), this);
	}
	public void insertItem(){
		for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
			if (inventory.getItem(entry.getKey()) == null) inventory.setItem(entry.getKey(), entry.getValue());
		}
	}
	public void formItem(){
		formBarTypeItem();
		formReqPreItem();
		formResPreItem();
		formConfirmItem();
		formLuckyItem();
		formOtherItem();
	}
	
	private void formOtherItem () {
		Map<String,Object> map = (Map<String, Object>) YamlService.getMap(Ymls.lotteryGUI.getPathInFolder(), "content");
		if (map == null) {
			Logger.log("&e[INFO] &f未找到GUI设置中的&b content &f参数");
			return;
		}
		List<String> filteredKeys = new ArrayList<>();
		filteredKeys.add("bar");
		filteredKeys.add("require_pre");
		filteredKeys.add("result_pre");
		filteredKeys.add("confirm");
		filteredKeys.add("lucky");
		map.keySet().stream().filter(key -> ! filteredKeys.contains(key)).forEach(key -> {
			Map<?,?> m = YamlService.getMap(Ymls.lotteryGUI.getPathInFolder(), "content." + key);
			ItemFactory fac = new ItemFactory();
			if ( m != null ) {
				setInfo(m, fac, UiAction.none, null);
			}
		});
	}
	
	private void setInfo(Map<?,?> map, ItemFactory fac, UiAction action, List<String> newLore){
		fac.setMaterial(Material.valueOf(map.get("material").toString().toUpperCase()));
		fac.setName(map.get("name") == null ? "&f未设置名称的组件" : map.get("name").toString());
		fac.setAmount(map.get("amount") == null ? 1 : Integer.parseInt(map.get("amount").toString()));
		fac.setData(map.get("data") == null ? null : Byte.parseByte(map.get("data").toString()));
		fac.setLore(map.get("lore") == null ? null : (List<String>) map.get("lore"));
		updateLore(fac, newLore);
		ItemStack item = fac.updateNbt(UiAction.key(), action.name());
		List<Integer> slots = RegexService.formListFromStr(map.get("slot").toString());
		if (slots != null) {
			slots.forEach(slot -> {
				items.put(slot, item);
			});
			actions.put(action.name(), slots);
		}
	}
	private void updateLore(ItemFactory fac, List<String> lore){
		if (lore == null || lore.isEmpty()) return;
		fac.setLore(lore);
	}
	private void formBarTypeItem(){
		Map<?, ?> map = YamlService.getMap(Ymls.lotteryGUI.getPathInFolder(), "content.bar");
		if (map == null) {
			Logger.log("&e[INFO] &f未找到GUI设置中的&b content.bar &f参数");
			return;
		}
		ItemFactory fac = new ItemFactory();
		setInfo(map, fac, UiAction.none, null);
	}
	private void formReqPreItem(){
		Map<?, ?> map = YamlService.getMap(Ymls.lotteryGUI.getPathInFolder(), "content.require_pre");
		if (map == null) {
			Logger.log("&e[INFO] &f未找到GUI设置中的&b content.require_pre &f参数");
			return;
		}
		ItemFactory fac = new ItemFactory();
		setInfo(map, fac, UiAction.req, LottInfoService.getReqLore(id));
	}
	private void formResPreItem(){
		Map<?, ?> map = YamlService.getMap(Ymls.lotteryGUI.getPathInFolder(), "content.result_pre");
		if (map == null) {
			Logger.log("&e[INFO] &f未找到GUI设置中的&b content.result_pre &f参数");
			return;
		}
		ItemFactory fac = new ItemFactory();
		setInfo(map, fac, UiAction.res, LottInfoService.getRewardLore(id));
	}
	private void formConfirmItem(){
		Map<?, ?> map = YamlService.getMap(Ymls.lotteryGUI.getPathInFolder(), "content.confirm");
		if (map == null) {
			Logger.log("&e[INFO] &f未找到GUI设置中的&b content.confirm &f参数");
			return;
		}
		ItemFactory fac = new ItemFactory();
		setInfo(map, fac, UiAction.confirm, null);
	}
	private void formLuckyItem(){
		Map<?, ?> map = YamlService.getMap(Ymls.lotteryGUI.getPathInFolder(), "content.lucky");
		if (map == null) {
			Logger.log("&e[INFO] &f未找到GUI设置中的&b content.lucky &f参数");
			return;
		}
		ItemFactory fac = new ItemFactory();
		setInfo(map, fac, UiAction.replace, null);
		this.luckySlot = Integer.parseInt(map.get("slot").toString());
	}
	public void reformLuckyItem(){
		List<Integer> list = actions.get(UiAction.replace.name());
		if (list == null || list.isEmpty()) return;
		ItemStack item = items.get(list.get(0));
		list.forEach(i -> inventory.setItem(i, item));
	}
	public void updateResultPre(Inventory inventory, List<String> lore){
		Bukkit.getScheduler().runTaskAsynchronously(LilisLottery.getInstance(), () -> {
			actions.get(UiAction.res.name()).forEach(i -> {
				ItemStack item = items.get(i).clone();
				ItemMeta meta = item.getItemMeta();
				meta.setLore(lore);
				item.setItemMeta(meta);
				inventory.setItem(i, item);
			});
		});
	}
	public void updateResultPre(Inventory inventory){
		ItemStack origin = items.get(actions.get(UiAction.res.name()).get(0));
		actions.get(UiAction.res.name()).forEach(i -> {
			inventory.setItem(i, origin);
		});
	}
}
