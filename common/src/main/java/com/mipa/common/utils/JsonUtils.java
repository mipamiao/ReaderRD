package com.mipa.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mipa.common.Constant.ExMsg;
import com.mipa.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Slf4j
@Component
public class JsonUtils {

	private static ObjectMapper objectMapper;

	@Autowired
	public void setObjectMapper(ObjectMapper mapper) {
		JsonUtils.objectMapper = mapper;
	}

	public static <T> T parseJson(String json, Class<T> type) {
		if (json == null || json.isEmpty()) {
			return null;
		}
		try {
			return objectMapper.readValue(json, type);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, ExMsg.JSON_PARSE_FAILED);
		}
	}

	public static String toJson(Object obj) {
		if (obj == null) {
			return null;
		}
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, ExMsg.JSON_TRANS_FAILED);
		}
	}


}
