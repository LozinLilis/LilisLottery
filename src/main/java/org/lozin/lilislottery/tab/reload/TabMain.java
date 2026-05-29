package org.lozin.lilislottery.tab.reload;

import org.lozin.tool.string.TabUtils;

import java.util.Arrays;
import java.util.List;

public class TabMain {
	public static boolean checker(String[] args) {
		return args.length == 1;
	}
	public static List<String> handle(String[] args) {
		return TabUtils.getCompletion(Arrays.asList("reload", "open"), args[0]);
	}
}
