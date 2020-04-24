package dev.cheerfun.pixivic.biz.analysis.tag.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/13 9:06 上午
 * @description SearchRecommend
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrendingTags extends Tag {
    private Illustration illustration;

    public TrendingTags(String name, String translatedName, Illustration illustration) {
        super(name, translatedName);
        this.illustration = illustration;
    }

}
