package dev.cheerfun.pixivic.common.model;

import com.fasterxml.jackson.annotation.JsonSetter;
import dev.cheerfun.pixivic.common.model.illust.ImageUrl;
import dev.cheerfun.pixivic.common.model.illust.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/28 9:19
 * @description illustration
 */
@Data
public class Illustration {
    private Integer id;
    @JsonSetter("artist_id")
    private Integer artistId;
    private String title;
    private String type;
    private String caption;
    @JsonSetter("artist")
    private ArtistPreView artistPreView;
    private List<Tag> tags;
    @JsonSetter("image_urls")
    private List<ImageUrl> imageUrls;
    private List<String> tools;
    @JsonSetter("create_date")
    private Date createDate;
    @JsonSetter("page_count")
    private Integer pageCount;
    private Integer width;
    private Integer height;
    @JsonSetter("sanity_level")
    private Integer sanityLevel;
    private Integer restrict;
    @JsonSetter("x_restrict")
    private Integer xRestrict;
    @JsonSetter("total_view")
    private Integer totalView;
    @JsonSetter("total_bookmarks")
    private Integer totalBookmarks;

    public void setArtistPreView(Integer id, String name, String account, String avatar) {
        this.artistPreView = new ArtistPreView(id, name, account, avatar);
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class ArtistPreView {
    private Integer id;
    private String name;
    private String account;
    private String avatar;
}

