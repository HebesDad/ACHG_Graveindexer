package org.achg.graveindex.views.components;

public class IntegerUtils {
	public static int safeParseInt(String str) {
		int i = 0;
		if (str != null) {
			try {
				i = Integer.parseInt(str.trim());
			} catch (NumberFormatException ex) {
				//
			}
		}
		return i;
	}
}
