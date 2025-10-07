package com.mipa.common.utils;

import org.springframework.util.PropertyPlaceholderHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ParamFill {
	public static String run(String srcStr, Map<String, Object> paramMap){
		PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}");
		String result = helper.replacePlaceholders(srcStr, key -> {
			Object value = paramMap.get(key);
			return value == null ? "" : value.toString();
		});
		return result;
	}

	public static Map<String, Object> transToMap(Object [] args){
		var paramMap = new HashMap<String, Object>();
		for(int i = 0 ; i < args.length; i++)
			paramMap.put("p" + i, args[i]);
		return paramMap;
	}
}
