package org.lozin.lilislottery.lottery.recipe;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;

@Getter @Setter
public class Reward {
	private String keyName;
	private List<String> commands;
	private Integer weight;
	private String desc;
	public Reward(@NonNull String keyName, @Nullable List<String> commands, @NonNull Integer weight, @Nullable String desc){
		this.keyName = keyName;
		this.commands = commands;
		this.weight = weight;
		this.desc = desc;
	}
	public void emitCommand(Player player){
		if (commands == null || commands.isEmpty()) return;
		commands.forEach(command -> {
			String cmd = command.replace("{player}", player.getName()).replaceAll("&(.)", "§$1");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		});
	}
	public static Reward getByKeyName(List<Reward> rewards, String keyName){
		for (Reward reward : rewards) if (reward.getKeyName().equals(keyName)) return reward;
		return null;
	}
}
