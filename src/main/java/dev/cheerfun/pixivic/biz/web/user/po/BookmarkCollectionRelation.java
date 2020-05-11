package dev.cheerfun.pixivic.biz.web.user.po;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/5/11 1:57 下午
 * @description BookmarkCOlletionRelation
 */
@Data
public class BookmarkCollectionRelation {
    private int id;
    @NotNull
    private String username;
    @NotNull
    private int userId;
    @NotNull
    private int collectionId;

}
