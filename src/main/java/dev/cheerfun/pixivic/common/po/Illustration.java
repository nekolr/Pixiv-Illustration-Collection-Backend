package dev.cheerfun.pixivic.common.po;

import com.fasterxml.jackson.annotation.JsonSetter;
import dev.cheerfun.pixivic.common.po.illust.ArtistPreView;
import dev.cheerfun.pixivic.common.po.illust.ImageUrl;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/07/28 9:19
 * @description illustration
 */
@Data
public class Illustration  {
    @JsonSetter("illust_id")
    protected Integer id;
    @JsonSetter("artist_id")
    protected Integer artistId;
    protected String title;
    protected String type;
    protected String caption;
    @JsonSetter("artist")
    protected ArtistPreView artistPreView;
    protected List<Tag> tags;
    @JsonSetter("image_urls")
    protected List<ImageUrl> imageUrls;
    protected List<String> tools;
    @JsonSetter("create_date")
    protected Date createDate;
    @JsonSetter("page_count")
    protected Integer pageCount;
    protected Integer width;
    protected Integer height;
    @JsonSetter("sanity_level")
    protected Integer sanityLevel;
    protected Integer restrict;
    @JsonSetter("x_restrict")
    protected Integer xRestrict;
    @JsonSetter("total_view")
    protected Integer totalView;
    @JsonSetter("total_bookmarks")
    protected Integer totalBookmarks;

    public void setArtistPreView(Integer id, String name, String account, String avatar) {
        this.artistPreView = new ArtistPreView(id, name, account, avatar);
    }

}


