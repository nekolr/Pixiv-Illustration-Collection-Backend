package dev.cheerfun.pixivic.biz.analysis.tag.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import dev.cheerfun.pixivic.biz.crawler.pixiv.dto.IllustrationDTO;
import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/14 6:55 下午
 * @description PixivTrendingTag
 */
@Data
public class PixivTrendingTag {
    private String tag;
    @JsonSetter("translated_name")
    private String translatedName;
    private IllustrationDTO illust;
}
