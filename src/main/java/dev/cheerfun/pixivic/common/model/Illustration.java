package dev.cheerfun.pixivic.common.model;

import dev.cheerfun.pixivic.common.model.illust.ImageUrl;
import dev.cheerfun.pixivic.common.model.illust.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/28 9:19
 * @description illustration
 */
@Data
public class Illustration {
    private Integer id;
    private Integer artistId;
    private String title;
    private String type;
    private String caption;
    private ArtistPreView artistPreView;
    private ArrayList<Tag> tags;
    private ArrayList<ImageUrl> imageUrls;
    private ArrayList<String> tools;
    private Date createDate;
    private Integer pageCount;
    private Integer width;
    private Integer height;
    private Integer sanityLevel;
    private Integer restrict;
    private Integer xRestrict;
    private Integer totalView;
    private Integer totalBookmarks;

    public void setArtistPreView(Integer id, String name, String account, String avatar) {
        this.artistPreView = new ArtistPreView(id, name, account, avatar);
    }
}

@Data
@AllArgsConstructor
class ArtistPreView {
    private Integer id;
    private String name;
    private String account;
    private String avatar;
}

