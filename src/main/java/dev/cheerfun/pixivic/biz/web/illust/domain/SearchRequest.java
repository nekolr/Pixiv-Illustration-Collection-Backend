package dev.cheerfun.pixivic.biz.web.illust.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/05 14:27
 * @description SearchRequest
 */
@Data
@NoArgsConstructor
public class SearchRequest {
    @NotBlank
    private String keyword;
    @NonNull
    @Max(60)
    @Min(1)
    private int pageSize;
    @NonNull
    @Max(1600)
    @Min(1)
    private int page;
    @NotBlank
    private String searchType;//搜索类型（原生、自动翻译、自动匹配词条）
    @NotBlank
    private String illustType;
    private int minWidth;
    private int minHeight;
    private String beginDate;
    private String endDate;
    @NonNull
    private int xRestrict;
    private int popWeight;
    private int minTotalBookmarks;
    private int minTotalView;
}

