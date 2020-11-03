package dev.cheerfun.pixivic.biz.web.illust.domain.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import dev.cheerfun.pixivic.biz.web.illust.exception.SearchException;
import lombok.Data;
import org.springframework.http.HttpStatus;

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
        try {
            return results.stream().filter(e -> e.getData().getOriginalUrls().stream().anyMatch(s -> s != null && s.contains("pixiv"))).map(e ->
                    e.getData().getIllustId());
        } catch (NullPointerException e) {
            throw new SearchException(HttpStatus.NOT_FOUND, "为找到搜索建议");
        }
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