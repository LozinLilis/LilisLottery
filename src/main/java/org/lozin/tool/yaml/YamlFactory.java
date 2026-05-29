package org.lozin.tool.yaml;

import lombok.Getter;
import org.lozin.lilislottery.main.LilisLottery;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class YamlFactory {
	private final String pathInFolder;
	private final File file;
	private Map<String, Object> map = new ConcurrentHashMap<>();
	@Getter private static final Map<String, YamlFactory> regFac = new HashMap<>();
	
	public YamlFactory(String pathInFolder) {
		this.pathInFolder = pathInFolder;
		Yaml yaml = new Yaml();
		this.file = new File(LilisLottery.getInstance().getDataFolder(), pathInFolder);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		try (FileInputStream fis = new FileInputStream(file)) {
			this.map = yaml.load(fis);
		} catch (Exception e) {
			e.printStackTrace();
		}
		regFac.put(pathInFolder, this);
	}
	public static YamlFactory getFac(String pathInFolder) {
		return regFac.containsKey(pathInFolder) ? regFac.get(pathInFolder) : new YamlFactory(pathInFolder);
	}
}
