package org.lozin.lilislottery.lottery.recipe;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.lozin.tool.yaml.YamlService;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter @Setter
public class Lucky {
	private String innerId;
	private Integer amount;
	private List<String> formulas;
	private String pathInFolder;
	@Getter private static Map<String, Lucky> cache = new ConcurrentHashMap<>();
	@Getter private static String detectKey;
	public Lucky(@NonNull Recipe recipe, @NonNull String innerId, @Nullable Integer amount, @NonNull List<String> formulas) {
		this.innerId = innerId;
		this.amount = amount == null ? 1 : amount;
		this.formulas = formulas;
		this.pathInFolder = recipe.getPathInFolder();
		cache.put(pathInFolder+"@"+innerId, this);
	}
	public static Lucky getById(Recipe recipe, String innerId){
		return Lucky.getCache().get(recipe.getPathInFolder()+"@"+innerId);
	}
	public static void regKey(){
		detectKey = YamlService.getString("config.yml","lucky_key");
	}
}
