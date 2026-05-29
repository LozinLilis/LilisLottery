package org.lozin.tool.string;

import java.util.List;
import java.util.stream.Collectors;

public class TabUtils {
	public static List<String> getCompletion (List<String> full, String arg){
		return full.stream().filter(s -> s.startsWith(arg)).collect(Collectors.toList());
	}
}
