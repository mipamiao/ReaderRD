package com.mipa.common.utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StringUtils {
	public static List<String> splitBySize(String input, int size) {
		List<String> result = new ArrayList<>();
		for (int i = 0; i < input.length(); i += size) {
			int end = Math.min(input.length(), i + size);
			result.add(input.substring(i, end));
		}
		return result;
	}
}
