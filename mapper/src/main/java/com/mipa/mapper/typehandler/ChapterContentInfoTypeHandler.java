package com.mipa.mapper.typehandler;


import com.mipa.common.utils.ChapterContentInfo;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@MappedTypes({ChapterContentInfo.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class ChapterContentInfoTypeHandler implements TypeHandler<ChapterContentInfo> {

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void setParameter(PreparedStatement ps, int i, ChapterContentInfo parameter, JdbcType jdbcType) throws SQLException {
		if (parameter != null) {
			try {
				String json = objectMapper.writeValueAsString(parameter);
				ps.setString(i, json);
			} catch (Exception e) {
				throw new SQLException("Failed to serialize ChapterContentInfo to JSON", e);
			}
		} else {
			ps.setString(i, null);
		}
	}

	@Override
	public ChapterContentInfo getResult(ResultSet rs, String columnName) throws SQLException {
		return parseJson(rs.getString(columnName));
	}

	@Override
	public ChapterContentInfo getResult(ResultSet rs, int columnIndex) throws SQLException {
		return parseJson(rs.getString(columnIndex));
	}

	@Override
	public ChapterContentInfo getResult(CallableStatement cs, int columnIndex) throws SQLException {
		return parseJson(cs.getString(columnIndex));
	}

	private ChapterContentInfo parseJson(String json) throws SQLException {
		if (json == null || json.isEmpty()) {
			return null;
		}
		try {
			return objectMapper.readValue(json, ChapterContentInfo.class);
		} catch (Exception e) {
			throw new SQLException("Failed to deserialize JSON to ChapterContentInfo", e);
		}
	}
}


