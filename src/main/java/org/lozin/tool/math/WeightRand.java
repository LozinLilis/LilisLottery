package org.lozin.tool.math;

import java.util.Map;

public class WeightRand {
	public static <T> T choose(Map<T, Integer> mapper) {
		int totalWeight = mapper.values().stream().mapToInt(Integer::intValue).sum();
		int random = (int) (Math.random() * totalWeight);
		for (Map.Entry<T, Integer> entry : mapper.entrySet()) {
			if (random < entry.getValue()) {
				return entry.getKey();
			}
			random -= entry.getValue();
		}
		return null;
	}
}
