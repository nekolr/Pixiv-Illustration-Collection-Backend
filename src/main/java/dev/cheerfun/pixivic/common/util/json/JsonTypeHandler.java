package dev.cheerfun.pixivic.common.util.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.cheerfun.pixivic.biz.notify.po.Actor;
import dev.cheerfun.pixivic.biz.web.illust.domain.SearchSuggestion;
import dev.cheerfun.pixivic.common.po.Artist;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.ArtistPreView;
import dev.cheerfun.pixivic.common.po.illust.ImageUrl;
import dev.cheerfun.pixivic.common.po.illust.Tag;
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
import java.util.List;

@MappedJdbcTypes(value = {JdbcType.OTHER}, includeNullJdbcType = true)
@MappedTypes({ArtistPreView.class, Artist.class, ArrayList.class, Tag.class, ImageUrl.class, SearchSuggestion.class, List.class, Illustration.class, Actor.class})
public class JsonTypeHandler<T> extends BaseTypeHandler<T> {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private Logger logger = LoggerFactory.getLogger(getClass());
    private Class<T> type;

    public JsonTypeHandler(Class<T> type) {
        if (type == null) throw new IllegalArgumentException("Type argument cannot be null");
        this.type = type;
    }

    /**
     * ??????????????????
     *
     * @param ps             PreparedStatement
     * @param parameterIndex ??????
     * @param parameter      ????????????
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
     * ???????????????null????????????
     *
     * @param resultSet  ?????????
     * @param columnName ??????
     */
    @Override
    public T getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        String jsonText = resultSet.getString(columnName);
        return readToObject(jsonText);
    }

    /**
     * ???????????????null????????????
     *
     * @param resultSet   ?????????
     * @param columnIndex columnIndex ??????
     */
    @Override
    public T getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        String jsonText = resultSet.getString(columnIndex);
        return readToObject(jsonText);
    }

    /**
     * ???????????????null????????????
     *
     * @param callableStatement CallableStatement
     * @param columnIndex       ??????
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
            return list;
        } catch (IOException e) {
            logger.error("???????????????????????????????????????{}", jsonText, e);
        }

        return null;
    }
}