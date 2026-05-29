package org.lozin.lilislottery.ui;

public enum UiAction {
	replace,
	confirm,
	none,
	req,
	res,
	;
	public static UiAction getByName(String name) {
		if (name == null) return null;
		for (UiAction action : values()) {
			if (action.name().equals(name)) {
				return action;
			}
		}
		return none;
	}
	public static boolean isValid(String name) {
		return getByName(name.toLowerCase()) != none;
	}
	public static String key() {
		return UiAction.class.getSimpleName();
	}
}
