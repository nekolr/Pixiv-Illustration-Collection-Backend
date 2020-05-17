package dev.cheerfun.pixivic.biz.web.collection.po;

import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/5/17 9:59 上午
 * @description CollectionStaticInfo
 */
@Data
public class CollectionStaticInfo {
    private Integer totalBookmarked;
    private Integer totalView;
    private Integer totalPeopleSeen;
    private Integer totalLiked;
}
