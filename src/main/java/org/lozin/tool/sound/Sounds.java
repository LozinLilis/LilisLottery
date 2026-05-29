package org.lozin.tool.sound;

import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.lozin.tool.string.Logger;
import org.lozin.tool.yaml.YamlService;

public enum Sounds {
	success(Sound.BLOCK_ANVIL_LAND, 0.5f, 0.85f),
	inlay(Sound.BLOCK_IRON_DOOR_OPEN, 0.5f, 1.5f),
	fail(Sound.ENTITY_ITEM_BREAK, 0.5f, 0.8f),
	error(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.2f),
	warn(Sound.ENTITY_ITEM_PICKUP, 0.5f, 0.3f),
	take_off(Sound.ITEM_ARMOR_EQUIP_ELYTRA, 0.5f, 0.9f),
	;
	
	Sounds(Sound sound, float volume, float pitch) {}
	private Sound sound;
	private float volume;
	private float pitch;
	@Getter private static boolean enable = true;
	public static Sounds getByString(String str) {
		for (Sounds sound : values()) {
			if (sound.name().equals(str)) {
				return sound;
			}
		}
		return null;
	}
	public static void reg(){
		String arg = YamlService.getString("config.yml", "enable_sound");
		if (arg == null) enable = true;
		enable = Boolean.parseBoolean(arg);
		Logger.log(enable ? "&f[&a√&f] 已启用 &eSound" : "&f[&c×&f] 已禁用 &eSound");
	}
	public static void play(Player player, Sounds s) {
		if (!enable) return;
		Sound sound = null; float volume = 0.5f, pitch = 0f;
		switch (s) {
		case success:
			sound = Sound.ITEM_ARMOR_EQUIP_GOLD;
			pitch = 0.7f;
			break;
		case inlay:
			sound = Sound.BLOCK_IRON_DOOR_OPEN;
			pitch = 1.5f;
			break;
		case fail:
			sound = Sound.ENTITY_ITEM_BREAK;
			pitch = 0.8f;
			break;
		case error:
			sound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
			pitch = 1.2f;
			break;
		case warn:
			sound = Sound.ENTITY_ITEM_PICKUP;
			pitch = 0.3f;
			break;
		case take_off:
			sound = Sound.ITEM_ARMOR_EQUIP_ELYTRA;
			pitch = 0.9f;
			break;
 		}
		 player.playSound(player.getLocation(), sound, volume, pitch);
	}
}
