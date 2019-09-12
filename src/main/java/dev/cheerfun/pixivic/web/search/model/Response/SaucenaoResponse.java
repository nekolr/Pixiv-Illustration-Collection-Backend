package dev.cheerfun.pixivic.web.search.model.Response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/10 14:53
 * @description SaucenaoResponse
 */
@Data
public class SaucenaoResponse {
    private List<Item> results;
}

@Data
class Item {
    private Header header;
    private Content data;
}

@Data
class Header {
    private String similarity;
    private String thumbnail;

}

@Data
class Content {
    @JsonSetter("ext_urls")
    private List<String> extUrls;
    private String title;
    @JsonSetter("pixiv_id")
    private String pixivId;
    @JsonSetter("member_name")
    private String memberName;
    @JsonSetter("member_id")
    private String memberId;
}