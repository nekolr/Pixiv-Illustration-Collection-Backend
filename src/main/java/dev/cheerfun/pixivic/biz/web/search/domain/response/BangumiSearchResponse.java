package dev.cheerfun.pixivic.biz.web.search.domain.response;

import com.fasterxml.jackson.annotation.JsonSetter;
import dev.cheerfun.pixivic.biz.web.search.domain.SearchSuggestion;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/14 15:16
 * @description BangumiSearchResult
 */
@Data
public class BangumiSearchResponse {
    @JsonSetter("list")
    private List<ResultItem> Result;

    public static List<SearchSuggestion> castToSearchSuggestionList(BangumiSearchResponse bangumiSearchResponse) {
        return bangumiSearchResponse!=null&&bangumiSearchResponse.Result!=null?bangumiSearchResponse.getResult().stream()
                .map(resultItem -> new SearchSuggestion(resultItem.getKeyword(), resultItem.getKeywordTranslated()))
                .collect(Collectors.toList()):new ArrayList<>();
    }
    public static double getAvgSum(BangumiSearchResponse bangumiSearchResponse){
        if(bangumiSearchResponse.getResult()!=null){
            OptionalDouble average = bangumiSearchResponse.getResult().stream().filter(b -> b.getRating() != null && b.getRating().getScore() != 0).mapToInt(resultItem -> resultItem.getRating().getScore()).average();
            return average.isPresent()?average.getAsDouble():0;
        }
        return 0;
    }
}

@Data
class ResultItem {
    @JsonSetter("name")
    private String keyword;
    @JsonSetter("name_cn")
    private String keywordTranslated;
    @JsonSetter("rating")
    private Rating rating;
}
@Data
class Rating{
    private int score;
}
