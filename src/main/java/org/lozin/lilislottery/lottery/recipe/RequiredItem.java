package org.lozin.lilislottery.lottery.recipe;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.lozin.tool.yaml.YamlService;

import java.util.List;

@Getter @Setter
public class RequiredItem {
	private String innerId;
	private Integer amount = 1;
	private String name;
	@Getter private static String detectKey;
	
	public RequiredItem(@NonNull String innerId, int amount, @NonNull String name) {
		this.innerId = innerId;
		this.amount = Math.max(amount, 0);
		this.name = name;
	}
	public static void regDetectKey() {
		detectKey = YamlService.getString("config.yml", "required_key");
	}
	public static String getNameById(List<RequiredItem> list, String id) {
		for (RequiredItem item : list) {
			if (item.getInnerId().equals(id)) return item.getName();
		}
		return "";
	}
}
