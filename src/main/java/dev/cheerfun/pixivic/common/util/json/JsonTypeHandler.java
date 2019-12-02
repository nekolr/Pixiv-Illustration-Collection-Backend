package dev.cheerfun.pixivic.common.util.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.web.search.model.SearchSuggestion;
import dev.cheerfun.pixivic.common.model.Artist;
import dev.cheerfun.pixivic.common.model.illust.ImageUrl;
import dev.cheerfun.pixivic.common.model.illust.Tag;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@MappedJdbcTypes(value = {JdbcType.OTHER}, includeNullJdbcType = true)
@MappedTypes({ Artist.class , ArrayList.class, Tag.class, ImageUrl.class, SearchSuggestion.class})
public class JsonTypeHandler<T> extends BaseTypeHandler<T> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static ObjectMapper objectMapper = new ObjectMapper();

    private Class<T> type;

    public JsonTypeHandler(Class<T> type) {
        if (type == null) throw new IllegalArgumentException("Type argument cannot be null");
        this.type = type;
    }

    /**
     * 设置非空参数
     *
     * @param ps             PreparedStatement
     * @param parameterIndex 参数
     * @param parameter      参数数组
     * @param jdbcType       jdbcType
     * @throws SQLException
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int parameterIndex, T parameter, JdbcType jdbcType)
            throws SQLException {
        String jsonText;
        try {
            jsonText = objectMapper.writeValueAsString(parameter);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        ps.setString(parameterIndex, jsonText);
    }

    /**
     * 获取可以为null的结果集
     *
     * @param resultSet  结果集
     * @param columnName 列名
     */
    @Override
    public T getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        String jsonText = resultSet.getString(columnName);
        return readToObject(jsonText);
    }

    /**
     * 获取可以为null的结果集
     *
     * @param resultSet   结果集
     * @param columnIndex columnIndex 列标
     */
    @Override
    public T getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        String jsonText = resultSet.getString(columnIndex);
        return readToObject(jsonText);
    }

    /**
     * 获取可以为null的结果集
     *
     * @param callableStatement CallableStatement
     * @param columnIndex       列号
     */
    @Override
    public T getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        String jsonText = callableStatement.getString(columnIndex);
        return readToObject(jsonText);
    }

    private T readToObject(String jsonText) {
        if (StringUtils.isEmpty(jsonText)) {
            return null;
        }

        try {
            T list = objectMapper.readValue(jsonText, type);
            return  list;
        } catch (IOException e) {
            logger.error("字符串反序列化失败字符串为{}",jsonText,e);
        }

        return null;
    }
}