package org.lozin.lilislottery.utils.item.nbt;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NbtService {
	private static String detector;
	public static String getCompound(ItemStack itemStack, String path) {
		if (itemStack == null || itemStack.getType() == Material.AIR) return null;
		List<String> paths = Arrays.stream(path.split("\\.")).filter(p -> !p.trim().isEmpty()).collect(Collectors.toList());
		NBTItem nbt = new NBTItem(itemStack);
		NBTCompound current = nbt.getCompound(paths.get(0));
		if (current == null) {
			if (paths.size() == 1 && nbt.hasTag(paths.get(0))) {
				return nbt.getString(paths.get(0));
			}else{
				return null;
			}
		}
		for (int i = 1; i < paths.size() -1; i++) {
			String key = paths.get(i);
			if (current == null || !current.hasTag(key)) {
				return null;
			}
			current = current.getCompound(key);
		}
		if (current != null) {
			return current.getString(paths.get(paths.size() - 1));
		}
		return null;
	}
	
	public static ItemStack setCompound(ItemStack itemStack, String path, Object value) {
		if (itemStack == null || itemStack.getType() == Material.AIR) return itemStack;
		List<String> paths = Arrays.stream(path.split("\\.")).filter(p -> !p.trim().isEmpty()).collect(Collectors.toList());
		NBTItem nbt = new NBTItem(itemStack);
		NBTCompound current = nbt.getCompound(paths.get(0));
		if (current == null) {
			nbt.setString(paths.get(0), value == null ? null : value.toString());
			return nbt.getItem();
		}
		for (int i = 1; i < paths.size() -1; i++){
			String key = paths.get(i);
			current = current.getOrCreateCompound(key);
		}
		current.setString(paths.get(paths.size() - 1), value == null ? null : value.toString());
		itemStack = nbt.getItem();
		return itemStack;
	}
}
