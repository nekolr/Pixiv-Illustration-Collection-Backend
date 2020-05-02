package dev.cheerfun.pixivic.biz.web.collection.dto;

import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/5/2 8:01 下午
 * @description UpdateIllustrationOrderDTO
 */
@Data
public class UpdateIllustrationOrderDTO {
    private Integer upIllustrationId;
    private Integer lowIllustrationId;
    private Integer reOrderIllustrationId;
}
