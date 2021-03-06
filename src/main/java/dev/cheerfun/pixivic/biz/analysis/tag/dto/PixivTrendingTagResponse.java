package dev.cheerfun.pixivic.biz.analysis.tag.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/14 6:51 下午
 * @description PixivTrendingTagResponse
 */
@Data
public class PixivTrendingTagResponse {
    @JsonAlias({"trend_tags", "trendTags"})
    private List<PixivTrendingTag> trendTags;
}
