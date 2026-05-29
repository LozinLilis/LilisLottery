package org.lozin.tool.string;

public class Color {
	public static String colorize(String str) {
		return str.replaceAll("&", "§").replaceAll("§§", "&");
	}
}
