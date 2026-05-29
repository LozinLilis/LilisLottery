package org.lozin.tool.string;

import org.lozin.lilislottery.main.LilisLottery;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RegexService {
	private static String precision;
	public static void initPrecision(){
		precision = LilisLottery.getInstance().getConfig().getString("precision");
		Logger.log("&f[&a√&f] 已设置精度: &e" + precision);
	}
	public static List<String> splitBy(String str, String splitter){
		return Arrays.asList(str.split(splitter));
	}
	public static List<Integer> formListFromStr(String str){
		List<String> temp = splitBy(str, ",");
		if (temp.isEmpty()) return null;
		List<Integer> result = new ArrayList<>();
		Pattern ft = Pattern.compile("\\d+\\s*-\\s*\\d+");
		for (String s : temp) {
			Matcher m = ft.matcher(s);
			if (m.matches()) {
				int start = Integer.parseInt(m.group().split("\\s*-\\s*")[0]);
				int end = Integer.parseInt(m.group().split("\\s*-\\s*")[1]);
				if (start > end) result.addAll(IntStream.rangeClosed(end, start).boxed().collect(Collectors.toList()));
				else result.addAll(IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList()));
			}
			else result.add(Integer.parseInt(s));
		}
		return result;
	}
	
	public static float toDecimal(float value) {
		String s = new DecimalFormat(precision).format(value);
		return Float.parseFloat(s);
	}
}
