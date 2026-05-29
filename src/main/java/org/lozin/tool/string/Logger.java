package org.lozin.tool.string;

import org.lozin.lilislottery.main.LilisLottery;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Logger {
	private static final Map<String, String> colors = new ConcurrentHashMap<>();
	static {
		colors.put("&0", "\u001B[30m");   // 黑色
		colors.put("&1", "\u001B[34m");   // 暗蓝
		colors.put("&2", "\u001B[32m");   // 暗绿
		colors.put("&3", "\u001B[36m");   // 藏青
		colors.put("&4", "\u001B[31m");   // 暗红
		colors.put("&5", "\u001B[35m");   // 暗紫
		colors.put("&6", "\u001B[33m");   // 金色
		colors.put("&7", "\u001B[37m");   // 浅灰
		colors.put("&8", "\u001B[90m");   // 深灰
		colors.put("&9", "\u001B[94m");   // 亮蓝
		colors.put("&a", "\u001B[92m");   // 亮绿
		colors.put("&b", "\u001B[96m");   // 亮青
		colors.put("&c", "\u001B[91m");   // 亮红
		colors.put("&d", "\u001B[95m");   // 亮紫
		colors.put("&e", "\u001B[93m");   // 亮黄
		colors.put("&f", "\u001B[97m");   // 白色
		colors.put("&r", "\u001B[0m");    // 重置
		colors.put("&l", "\u001B[1m");    // 粗体
		colors.put("&o", "");             // 斜体
		colors.put("&n", "\u001B[4m");    // 下划线
		colors.put("&m", "");             // 删除线
		colors.put("&k", "\u001B[5m");    // 闪烁
	}
	public static void log(String str) {
		for (Map.Entry<String, String> entry : colors.entrySet()){
			str = str.replace(entry.getKey(), entry.getValue());
		}
		LilisLottery.getInstance().getLogger().info(str+"\u001B[0m");
	}
	public static void log(String... str) {
		for (String s : str) {
			log(s);
		}
	}
}
