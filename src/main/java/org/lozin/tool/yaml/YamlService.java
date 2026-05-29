package org.lozin.tool.yaml;

import org.lozin.lilislottery.main.LilisLottery;
import org.lozin.tool.file.FileService;
import org.lozin.tool.string.Logger;
import org.lozin.tool.string.RegexService;

import java.util.List;
import java.util.Map;

public interface YamlService {
	static Object get(String pathInFolder, String dic){
		YamlFactory fac = YamlFactory.getFac(pathInFolder);
		if (fac == null) {
			Logger.log("&c[WARN] &f文件: &e"+pathInFolder+" &f的缓存未找到");
			return null;
		}
		Object current = fac.getMap();
		if (current == null) return null;
		List<String> dicList = RegexService.splitBy(dic, "\\.");
		for (String dict : dicList) {
			if (current instanceof Map) {
				if (((Map<?, ?>) current).containsKey(dict)) current = ((Map<?, ?>) current).get(dict);
				else return null;
			}
			else if (current instanceof List) {
				List<?> list = (List<?>) current;
				try {
					int index = Integer.parseInt(dict);
					if (index >= 0 && index < list.size()) current = list.get(index);
					else return null;
				}
				catch (Exception e) {
					return null;
				}
			}
			else return null;
		}
		return current;
	}
	static Object getAll(String pathInFolder){
		return YamlFactory.getFac(pathInFolder).getMap();
	}
	static String getString(String pathInFolder, String dic){
		Object obj = get(pathInFolder, dic);
		if (obj == null) {
			Logger.log("&c[WARN] &f文件: &e"+pathInFolder+" &f的 "+dic+" 未找到 String");
			return null;
		}
		return obj.toString();
	}
	static Map<?,?> getMap(String pathInFolder, String dic){
		Object obj = get(pathInFolder, dic);
		if (obj == null) {
			Logger.log("&c[WARN] &f文件: &e"+pathInFolder+" &f的 "+dic+" 未找到 Map");
			return null;
		}
		return (Map<?,?>) obj;
	}
	static List<?> getList(String pathInFolder, String dic){
		Object obj = get(pathInFolder, dic);
		return obj == null ? null : (List<?>) obj;
	}
	static Number getNum(String pathInFolder, String dic){
		Object obj = get(pathInFolder, dic);
		if (obj == null) {
			Logger.log("&c[WARN] &f文件: &e"+pathInFolder+" &f的 "+dic+" 未找到 Number");
			return null;
		}
		return (Number) obj;
	}
	static void removeFac(String pathInFolder){
		YamlFactory.getRegFac().remove(pathInFolder);
	}
	static void clearFac(){
		YamlFactory.getRegFac().clear();
	}
	static void reloadFac(){
		clearFac();
		FileService.regFacts(LilisLottery.getInstance().getDataFolder());
	}
	static boolean regFac(String pathInFolder){
		return YamlFactory.getFac(pathInFolder) != null;
	}
}
