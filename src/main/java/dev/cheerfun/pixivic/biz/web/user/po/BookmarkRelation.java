package dev.cheerfun.pixivic.biz.web.user.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/19 9:29
 * @description Bookmark
 */
@Data
@NoArgsConstructor
public class BookmarkRelation {
    private int id;
    @NonNull
    private String username;
    @NonNull
    private int userId;
    @NonNull
    private int illustId;
}
