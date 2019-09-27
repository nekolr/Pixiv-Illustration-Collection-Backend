package dev.cheerfun.pixivic.crawler.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.cheerfun.pixivic.common.model.Illustration;
import dev.cheerfun.pixivic.common.model.illust.ImageUrl;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/01 9:36
 * @description IllustrationDTO
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class IllustrationDTO {
    private String id;
    private String illust_id;//pixiv_id
    private String title;
    private String type;
    private String caption;
    private User user;
    private List<Tag> tags;
    private List<String> tools;
    private Date create_date;
    private Integer page_count;
    private Integer width;
    private Integer height;
    private Integer sanity_level;//色情指数(大于5上传其他图床)
    private MetaSinglePage meta_single_page;
    private List<MetaPage> meta_pages;
    private ImageUrls image_urls;
    private Integer restrict;
    private Integer x_restrict;
    private Integer total_view;
    private Integer total_bookmarks;

    public static Illustration castToIllustration(IllustrationDTO illustrationDTO) {
        if(illustrationDTO!=null){
            Illustration illustration = new Illustration();
            //System.out.println(illustrationDTO);
            illustration.setId(Integer.valueOf(illustrationDTO.getId()));
            illustration.setTitle(illustrationDTO.getTitle());
            illustration.setType(illustrationDTO.getType());
            illustration.setCaption(illustrationDTO.getCaption());
            User user = illustrationDTO.getUser();
            illustration.setArtistPreView(user.getId(), user.getName(), user.getAccount(), user.getProfile_image_urls().getMedium());
            illustration.setArtistId(user.getId());
            illustration.setTags(new ArrayList<>());
            List<dev.cheerfun.pixivic.common.model.illust.Tag> tags = illustration.getTags();
            illustrationDTO.getTags().forEach(t -> {
                dev.cheerfun.pixivic.common.model.illust.Tag tag = new dev.cheerfun.pixivic.common.model.illust.Tag();
                tag.setName(t.getName());
                tag.setTranslatedName(t.getTranslated_name());
                tags.add(tag);
            });
            illustration.setTools(illustrationDTO.getTools());
            illustration.setCreateDate(illustrationDTO.getCreate_date());
            illustration.setPageCount(illustrationDTO.getPage_count());
            illustration.setImageUrls(new ArrayList<>());
            List<ImageUrl> imageUrls = illustration.getImageUrls();
            if (illustrationDTO.getPage_count() > 1) {
                illustrationDTO.getMeta_pages().forEach(metaPage -> {
                    ImageUrl imageUrl = new ImageUrl();
                    ImageUrls image_urlsTemp = metaPage.getImage_urls();
                    imageUrl.setLarge(image_urlsTemp.getLarge());
                    imageUrl.setMedium(image_urlsTemp.getMedium());
                    imageUrl.setOriginal(image_urlsTemp.getOriginal());
                    imageUrl.setSquareMedium(image_urlsTemp.getSquare_medium());
                    imageUrls.add(imageUrl);
                });
            } else {
                ImageUrl imageUrl = new ImageUrl();
                ImageUrls image_urlsTemp = illustrationDTO.getImage_urls();
                imageUrl.setLarge(image_urlsTemp.getLarge());
                imageUrl.setMedium(image_urlsTemp.getMedium());
                imageUrl.setOriginal(illustrationDTO.getMeta_single_page().getOriginal_image_url());
                imageUrl.setSquareMedium(image_urlsTemp.getSquare_medium());
                imageUrls.add(imageUrl);
            }
            illustration.setWidth(illustrationDTO.getWidth());
            illustration.setHeight(illustrationDTO.getHeight());
            illustration.setSanityLevel(illustrationDTO.getSanity_level());
            illustration.setRestrict(illustrationDTO.getRestrict());
            illustration.setXRestrict(illustrationDTO.getX_restrict());
            illustration.setTotalView(illustrationDTO.getTotal_view());
            illustration.setTotalBookmarks(illustrationDTO.getTotal_bookmarks());
            return illustration;
        }
        return null;
    }
}

@Data
class ImageUrls {
    String square_medium;
    String medium;
    String large;
    String original;
}

@Data
class MetaPage {
    private ImageUrls image_urls;
}

@Data
class MetaSinglePage {
    String original_image_url;
}

@Data
class Tag {
    private String name;
    private String translated_name;
}





