package dev.cheerfun.pixivic.biz.web.collection.dto;

import dev.cheerfun.pixivic.biz.web.collection.po.Collection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/4 9 PM
 * @description ColletionDigest
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionDigest {
    private Integer id;
    private String title;
    private String caption;

    public static CollectionDigest castByCollection(Collection collection) {
        return new CollectionDigest(collection.getId(), collection.getTitle(), collection.getCaption());
    }

}
