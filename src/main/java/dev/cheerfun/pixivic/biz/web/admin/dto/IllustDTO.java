package dev.cheerfun.pixivic.biz.web.admin.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/4/24 3:18 下午
 * @description IllustDTO
 */
@Data
public class IllustDTO {
    @NotNull
    private Integer id;
    @NotNull
    private Integer xRestrict;
    @NotNull
    private Integer sanityLevel;
}
