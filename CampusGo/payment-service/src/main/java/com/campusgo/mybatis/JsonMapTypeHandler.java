package com.campusgo.mybatis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class JsonMapTypeHandler extends BaseTypeHandler<Map<String, Object>> {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<HashMap<String, Object>> TYPE =
            new TypeReference<HashMap<String, Object>>() {};

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Map<String, Object> param, JdbcType jdbcType)
            throws SQLException {
        try {
            ps.setString(i, MAPPER.writeValueAsString(param));
        } catch (Exception e) {
            throw new SQLException("Failed to serialize json", e);
        }
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return parse(json);
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return parse(json);
    }

    @Override
    public Map<String, Object> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return parse(json);
    }

    private Map<String, Object> parse(String json) throws SQLException {
        if (json == null || json.isEmpty()) return null;
        try {
            return MAPPER.readValue(json, TYPE);
        } catch (Exception e) {
            throw new SQLException("Failed to deserialize json", e);
        }
    }
}
