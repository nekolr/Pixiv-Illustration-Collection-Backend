package dev.cheerfun.pixivic.biz.crawler.pixiv.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/27 21:51
 * @description IllustrationDetailDTO
 */
@Data
public class IllustrationDetailDTO {
    @JsonAlias({"illust", "illustrationDTO"})
    private IllustrationDTO illustrationDTO;
}
