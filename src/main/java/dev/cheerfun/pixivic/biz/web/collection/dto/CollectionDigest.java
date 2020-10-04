package dev.cheerfun.pixivic.biz.web.collection.dto;

import dev.cheerfun.pixivic.biz.web.collection.po.Collection;
import lombok.Data;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/4 9:19 PM
 * @description ColletionDigest
 */
@Data
public class CollectionDigest {
    private Integer id;
    private String title;

    public static CollectionDigest castByCollection(Collection collection) {
        return new CollectionDigest(collection.getId(), collection.getTitle());
    }

    public CollectionDigest(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    public CollectionDigest() {
    }
}
