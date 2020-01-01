package dev.cheerfun.pixivic.biz.web.dto;

import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 20-1-1 下午4:06
 * @description IllustrationWithLikeInfo
 */
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class IllustrationWithLikeInfo extends Illustration {
    private Boolean isLiked;
}
