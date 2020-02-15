package dev.cheerfun.pixivic.biz.web.search.domain.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/10 14:53
 * @description SaucenaoResponse
 */
@Data
public class SaucenaoResponse {
    private List<Item> results;

    public Stream<Integer> getPixivIdList() {
        return results.stream().filter(e -> e != null && e.getData() != null && e.getData().getOriginalUrls() != null && e.getData().getIllustId() != null && e.getData().getOriginalUrls().stream().anyMatch(s -> s != null && s.contains("pixiv"))).map(e ->
                e.getData().getIllustId());
    }
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
    private List<String> originalUrls;
    private String title;
    @JsonSetter("pixiv_id")
    private Integer illustId;
    @JsonSetter("member_name")
    private String artistName;
    @JsonSetter("member_id")
    private String artistId;
}