package org.lozin.lilislottery.utils.item;

import com.google.common.collect.ImmutableMap;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.lozin.lilislottery.lottery.recipe.RequiredItem;
import org.lozin.lilislottery.utils.item.nbt.NbtService;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ItemFactory {
	private String name;
	private Material material;
	private int amount = 1;
	private Byte data;
	private List<String> lore;
	private String innerId;
	private List<Map<Enchantment, Integer>> enchant;
	private ItemStack item;
	private NBTCompound nbt;
	public ItemFactory(ItemStack item){
		this.item = item;
		toFac(item);
	}
	public ItemFactory(){}
	public ItemFactory(String innerId, Material material, String name, List<String> lore){
		this.innerId = innerId;
		this.material = material;
		if (name != null) name = name.replaceAll("&", "§");
		this.name = name;
		this.lore = lore;
		if (lore != null && !lore.isEmpty()) {
			List<String> newLore = new LinkedList<>();
			for (String line : lore) {
				newLore.add(line.replaceAll("&", "§"));
			}
			this.lore = newLore;
		}
		this.item = toItem();
	}
	public static ItemFactory toFac(ItemStack item){
		if (!valid(item)) return null;
		ItemFactory fac = new ItemFactory();
		fac.item = item;
		fac.material = item.getType();
		if (item.getItemMeta() != null) {
			if (item.getItemMeta().hasDisplayName()) fac.name = item.getItemMeta().getDisplayName();
			if (item.getItemMeta().hasLore()) fac.lore = item.getItemMeta().getLore();
			if (item.getItemMeta().hasEnchants()) {
				for (Enchantment ench : item.getItemMeta().getEnchants().keySet()) {
					fac.enchant.add(ImmutableMap.of(ench, item.getItemMeta().getEnchants().get(ench)));
				}
			}
			fac.amount = item.getAmount();
			if (item.getData() != null) fac.data = item.getData().getData();
			fac.innerId = "__temp";
		}
		fac.nbt = new NBTItem(item);
		return fac;
	}
	public static boolean valid(ItemStack item){
		return item != null && item.getType() != Material.AIR;
	}
	
	public boolean valid(){
		return valid(item);
	}
	public ItemStack toItem(){
		Material material = this.material;
		ItemStack item = new ItemStack(material == null ? Material.STONE : material);
		if (!valid(item)) return null;
		item.setAmount(amount);
		ItemMeta meta = item.getItemMeta();
		if (name != null) meta.setDisplayName(name);
		if (lore != null) meta.setLore(lore);
		if (enchant != null) {
			for (Map<?,?> map: enchant) {
				for (Map.Entry<?,?> entry: map.entrySet()) {
					item.addEnchantment(Enchantment.getByName(entry.getKey().toString()), Integer.parseInt(entry.getValue().toString()));
				}
			}
		}
		if (data != null) item.setDurability(data);
		item.setItemMeta(meta);
		this.item = item;
		return this.item;
	}
	public ItemStack updateNbt(String path, Object value){
		if (this.item == null) toItem();
		this.item = NbtService.setCompound(this.item, path, value);
		this.nbt = new NBTItem(this.item);
		return this.item;
	}
	public void setName(String name){
		this.name = name.replaceAll("&(.)", "§$1");
	}
	public void setLore(List<String> lore){
		if (lore != null && !lore.isEmpty()) {
			List<String> newLore = new LinkedList<>();
			for (String line : lore) {
				newLore.add(line.replaceAll("&(.)", "§$1"));
			}
			this.lore = newLore;
		}
	}
	public static String getInnerId(ItemStack item, String path){
		return NbtService.getCompound(item, path);
	}
	public static String getCostId(ItemStack item){
		return getInnerId(item, RequiredItem.getDetectKey());
	}
}
