package org.lozin.lilislottery;

import lombok.Getter;

@Getter
public enum Ymls {
	lotteryGUI("lotteryGUI.yml"),
	;
	private final String pathInFolder;
	Ymls(String pathInFolder) {
		this.pathInFolder = pathInFolder;
	}
}
