package com.campusgo.mybatis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.util.Collections;
import java.util.List;

public class StringListJsonTypeHandler extends BaseTypeHandler<List<String>> {
    private static final ObjectMapper M = new ObjectMapper();
    private static final TypeReference<List<String>> T = new TypeReference<>() {};

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setString(i, M.writeValueAsString(parameter == null ? Collections.emptyList() : parameter));
        } catch (Exception e) {
            throw new SQLException("Serialize tags to JSON failed", e);
        }
    }

    @Override public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException { return parse(rs.getString(columnName)); }
    @Override public List<String> getNullableResult(ResultSet rs, int columnIndex)   throws SQLException { return parse(rs.getString(columnIndex)); }
    @Override public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException { return parse(cs.getString(columnIndex)); }

    private List<String> parse(String json) throws SQLException {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try { return M.readValue(json, T); } catch (Exception e) { throw new SQLException("Parse tags JSON failed: " + json, e); }
    }
}
