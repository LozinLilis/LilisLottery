package org.lozin.lilislottery.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.lozin.lilislottery.lottery.LottInfoService;
import org.lozin.lilislottery.lottery.cache.RecipeCache;
import org.lozin.lilislottery.lottery.recipe.Lucky;
import org.lozin.lilislottery.lottery.recipe.Recipe;
import org.lozin.lilislottery.lottery.recipe.Reward;
import org.lozin.lilislottery.main.LilisLottery;
import org.lozin.lilislottery.ui.UiAction;
import org.lozin.lilislottery.ui.UiFactory;
import org.lozin.lilislottery.utils.item.ItemCost;
import org.lozin.lilislottery.utils.item.ItemFactory;
import org.lozin.lilislottery.utils.item.nbt.NbtService;
import org.lozin.tool.sound.Sounds;
import org.lozin.tool.string.Logger;
import org.lozin.tool.string.Message;
import org.lozin.tool.yaml.YamlService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class UiHandler extends Event {
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	@Override public HandlerList getHandlers() {
		return handlers;
	}
	private UiFactory ui;
	private final Player player;
	private int clickedSlot;
	private Inventory clickedInventory;
	private ItemStack cursorItem = null;
	private InventoryClickEvent event;
	private ItemStack clickedItem;
	private static Map<UUID, List<Reward>> playerRewardMap = new ConcurrentHashMap<>();
	
	public UiHandler(UiFactory ui, Player player, int clickedSlot, Inventory clickedInventory) {
		this.ui = ui;
		this.player = player;
		this.clickedSlot = clickedSlot;
		this.clickedInventory = clickedInventory;
	}
	public UiHandler(UiFactory ui, InventoryClickEvent event) {
		this.ui = ui;
		this.event = event;
		this.player = (Player) event.getWhoClicked();
		this.clickedSlot = event.getRawSlot();
		this.clickedInventory = event.getClickedInventory();
		this.cursorItem = event.getCursor();
		this.clickedItem = event.getCurrentItem();
	}
	public void call() {
		Bukkit.getPluginManager().callEvent(this);
	}
	public boolean isSafe() {
		if (ui == null) return false;
		if (clickedInventory == player.getInventory()) return false;
		return true;
	}
	public void clickEvent(){
		ItemStack item = event.getCurrentItem();
		if (!ItemFactory.valid(item)) return;
		String action = NbtService.getCompound(item, UiAction.class.getSimpleName());
		if (action == null) return;
		clickNone(action);
		clickReq(action);
		clickRes(action);
		clickReplace(action);
		clickRecipeConfirm(action);
	}
	private void clickReq(String action) {
		if (!UiAction.getByName(action).equals(UiAction.req)) return;
		setCancelled(true);
	}
	private void clickNone(String action) {
		if (!UiAction.getByName(action).equals(UiAction.none)) return;
		setCancelled(true);
	}
	private void clickReplace(String action) {
		if (!UiAction.getByName(action).equals(UiAction.replace)) return;
		if (ui.isReplaced()) return;
		if (!ItemFactory.valid(cursorItem)) {
			setCancelled(true);
			return;
		}
		if (NbtService.getCompound(cursorItem, Lucky.getDetectKey()) == null) {
			setCancelled(true);
			Message.colorize(player, "&c[WARN] &f无法将非加成类物品放置在这里");
			Sounds.play(player, Sounds.warn);
			return;
		}
		Recipe recipe = RecipeCache.getById(ui.getId());
		if (recipe == null) {
			setCancelled(true);
			Message.colorize(player, "&c[ERROR] &f无法找到此配方");
			Sounds.play(player, Sounds.error);
			return;
		}
		Lucky lucky = getLuckyByItem(recipe, cursorItem);
		if (!enoughLuckyItem(lucky, cursorItem)){
			if (lucky != null) {
				Message.colorize(player, "&c[WARN] &f加成物品的数量不足, 缺少 &e" + (lucky.getAmount()-cursorItem.getAmount())+" &f个");
				Sounds.play(player, Sounds.warn);
			}
			else {
				Message.colorize(player, "&c[ERROR] &f该加成物无法影响这个配方");
				Sounds.play(player, Sounds.error);
			}
			setCancelled(true);
			return;
		}
		clickedInventory.setItem(clickedSlot, cursorItem);
		Sounds.play(player, Sounds.inlay);
		setCancelled(true);
		playerRewardMap.put(player.getUniqueId(), recipe.reformRewards(lucky));
		List<String> lore = LottInfoService.getRewardLore(playerRewardMap.get(player.getUniqueId()));
		if (lore != null) {
			//Message.colorize(player, lore);
			ui.updateResultPre(clickedInventory, lore);
		}
		player.setItemOnCursor(new ItemStack(Material.AIR));
		ui.setReplaced(true);
	}
	public void takeOffReplacer(){
		if (!ui.isReplaced()) return;
		if (!isNormalClick()) return;
		if (!ItemFactory.valid(cursorItem)) {
			if (player.getInventory().firstEmpty() == -1) {
				Message.colorize(player, "&c[WARN] &e背包已满, 已将该物品丢出");
				player.getWorld().dropItem(player.getLocation(), clickedItem);
			}
			else player.getInventory().addItem(clickedItem);
			Sounds.play(player, Sounds.take_off);
			setCancelled(true);
			player.setItemOnCursor(new ItemStack(Material.AIR));
			ui.reformLuckyItem();
			ui.setReplaced(false);
			Recipe recipe = RecipeCache.getById(ui.getId());
			ui.updateResultPre(clickedInventory);
		}
		else {
			if (player.getInventory().firstEmpty() == -1) {
				Message.colorize(player, "&c[WARN] &e背包已满, 已将该物品丢出");
				Sounds.play(player, Sounds.warn);
				player.getWorld().dropItem(player.getLocation(), clickedItem);
			}
			else player.getInventory().addItem(clickedItem);
			Sounds.play(player, Sounds.take_off);
			if (NbtService.getCompound(cursorItem, Lucky.getDetectKey()) == null) {
				setCancelled(true);
				Message.colorize(player, "&c[WARN] &f无法将非加成类物品放置在这里");
				Sounds.play(player, Sounds.warn);
				ui.reformLuckyItem();
				ui.updateResultPre(clickedInventory);
				ui.setReplaced(false);
//				if (player.getInventory().firstEmpty() == -1) {
//					Message.colorize(player, "&c[WARN] &e背包已满, 已将该物品丢出");
//					Sounds.play(player, Sounds.warn);
//					player.getWorld().dropItem(player.getLocation(), cursorItem);
//				}
//				else player.getInventory().addItem(cursorItem);
//				player.setItemOnCursor(new ItemStack(Material.AIR));
				return;
			}
			Recipe recipe = RecipeCache.getById(ui.getId());
			if (recipe == null) {
				setCancelled(true);
				Message.colorize(player, "&c[ERROR] &f无法找到此配方");
				Sounds.play(player, Sounds.error);
				//ui.setReplaced(true);
				return;
			}
			Lucky lucky = getLuckyByItem(recipe, cursorItem);
			if (!enoughLuckyItem(lucky, cursorItem)){
				if (lucky != null) {
					Message.colorize(player, "&c[WARN] &f加成物品的数量不足, 缺少 &e" + (lucky.getAmount()-cursorItem.getAmount())+" &f个");
					Sounds.play(player, Sounds.warn);
				}
				else {
					Message.colorize(player, "&c[ERROR] &f该加成物不适用于此配方");
					Sounds.play(player, Sounds.error);
				}
				setCancelled(true);
				ui.reformLuckyItem();
				ui.updateResultPre(clickedInventory);
				ui.setReplaced(false);
				return;
			}
			clickedInventory.setItem(clickedSlot, cursorItem);
			setCancelled(true);
			playerRewardMap.put(player.getUniqueId(), recipe.reformRewards(lucky));
			List<String> lore = LottInfoService.getRewardLore(playerRewardMap.get(player.getUniqueId()));
			if (lore != null) {
				//Message.colorize(player, lore);
				ui.updateResultPre(clickedInventory, lore);
			}
			player.setItemOnCursor(new ItemStack(Material.AIR));
			Sounds.play(player, Sounds.inlay);
			setCancelled(true);
		}
	}
	private boolean isDebug() {
		return LilisLottery.isDebug();
	}
	private void clickRecipeConfirm(String action) {
		if (!UiAction.getByName(action).equals(UiAction.confirm)) return;
		List<Reward> rewards = playerRewardMap.get(player.getUniqueId());
		if (rewards == null || rewards.isEmpty()) rewards = RecipeCache.getById(ui.getId()).getRewards();
		if (rewards == null || rewards.isEmpty()){
			Message.colorize(player, "&c[ERROR] &e奖池中不存在奖品, 请联系管理员进行修复");
			Sounds.play(player, Sounds.error);
			return;
		}
		setCancelled(true);
		Recipe recipe = RecipeCache.getById(ui.getId());
		if (recipe == null) {
			if (isDebug()) {
				Logger.log("&6[调试信息] &f玩家 &e" + player.getName() + " &f正在抽取的配方&c不存在");
			}
			return;
		}
		if (isDebug()) {
			Logger.log("&6[调试信息] &f玩家 &e" + player.getName() + " &f正在抽取奖池 &b" + recipe.getId() + " &f奖品列表:");
			if ( ! rewards.isEmpty() ) for (Reward reward : rewards) {
				Logger.log("- &7" + (reward == null ? "null" : (reward.getKeyName() + " &8(" + reward.getDesc() + ")&7: &b" + reward.getWeight() + " &7权重 &f-> &6" + reward.getCommands())));
			}
			else Logger.log("- &7空");
		}
		try {
			//if (!recipe.costRequiredItem(player)) return;
			if (!ItemCost.cost(player, recipe.getRequiredItems())) return;
			recipe.costLuckyItem(ui, ui.getLuckySlot(), player);
		}
		catch (Exception e) {
			return;
		}
		Reward reward = recipe.chooseReward(rewards);
		if (isDebug()) {
			Logger.log("&6[调试信息] &f玩家 &e" + player.getName() + " &f抽中奖品 &b" + reward.getKeyName());
		}
		reward.emitCommand(player);
		Sounds.play(player, Sounds.success);
		String lucky_key = YamlService.getString("config.yml","lucky_key");
		int lucky_slot = ui.getLuckySlot();
		if (NbtService.getCompound(ui.getInventory().getItem(lucky_slot), lucky_key) == null) playerRewardMap.remove(player.getUniqueId());
	}
	private void clickRes(String action){
		if (!action.equals(UiAction.res.name())) return;
		setCancelled(true);
	}
	private Lucky getLuckyByItem(Recipe recipe, ItemStack itemStack){
		if (recipe == null) return null;
		String innerId = Lucky.getDetectKey();
		if (innerId == null || innerId.isEmpty()) {
			Logger.log("&c[ERROR] &f未找到&9 config.yml 中的&b lucky_key &f参数");
			return null;
		}
		String id = NbtService.getCompound(itemStack, innerId);
		if (id == null) return null;
		return Lucky.getById(recipe, id);
	}
	private UiAction getClickType(){
		return UiAction.getByName(NbtService.getCompound(clickedItem, UiAction.key()));
	}
	private boolean isNormalClick(){
		return NbtService.getCompound(clickedItem, UiAction.key()) == null;
	}
	private boolean enoughLuckyItem(Lucky lucky, ItemStack itemStack){
		if (lucky == null) return false;
		int amount = lucky.getAmount();
		int itemAmount = itemStack.getAmount();
		return itemAmount >= amount;
	}
}