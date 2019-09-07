package dev.cheerfun.pixivic.web.search.controller;

import dev.cheerfun.pixivic.common.model.Illustration;
import dev.cheerfun.pixivic.common.model.Result;
import dev.cheerfun.pixivic.web.search.model.Response.PixivSearchCandidatesResponse;
import dev.cheerfun.pixivic.web.search.model.SearchSuggestion;
import dev.cheerfun.pixivic.web.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.index.query.functionscore.ScriptScoreFunctionBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.*;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/14 22:45
 * @description SearchController
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
//@PermissionRequired
public class SearchController {
    private final SearchService searchService;
    private final RestHighLevelClient elasticsearch;

    @GetMapping("/test")
    public String test() throws IOException {
        //从内到外构建

        BoostingQueryBuilder boost1 = QueryBuilders.boostingQuery(QueryBuilders.matchQuery("tags.name", "星空"), QueryBuilders.matchQuery("tags.translated_name.keyword", "")).negativeBoost(0.865f);
        BoostingQueryBuilder boost2 = QueryBuilders.boostingQuery(QueryBuilders.matchQuery("tags.name", "少女"), QueryBuilders.matchQuery("tags.translated_name.keyword", "")).negativeBoost(0.865f);
        NestedQueryBuilder nested1 = QueryBuilders.nestedQuery("tags", boost1, ScoreMode.Max);
        NestedQueryBuilder nested2 = QueryBuilders.nestedQuery("tags", boost2, ScoreMode.Max);
        List<NestedQueryBuilder> nestedQueryBuilders = new ArrayList<>();
        nestedQueryBuilders.add(nested1);
        nestedQueryBuilders.add(nested2);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should().addAll(nestedQueryBuilders);
        TermQueryBuilder term1 = QueryBuilders.termQuery("type", "illust");
        TermQueryBuilder term2 = QueryBuilders.termQuery("x_restrict", 0);
        RangeQueryBuilder term3 = QueryBuilders.rangeQuery("create_date" ).gte("2010-01-24");
        List<QueryBuilder> filter = new ArrayList<>();
        filter.add(term1);
        filter.add(term2);
        filter.add(term3);
        boolQueryBuilder.filter().addAll(filter);
        Map<String, Object> params = new HashMap<>();
        params.put("a", 10);
        // 脚本
        String scriptStr = "return params.a;";
        Script script = new Script(ScriptType.INLINE, "painless", scriptStr, params);

        ScriptScoreFunctionBuilder scriptScoreFunctionBuilder = ScoreFunctionBuilders.scriptFunction(script);
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(boolQueryBuilder, scriptScoreFunctionBuilder);

        SearchRequest searchRequest = new SearchRequest("illust");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query( functionScoreQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        System.out.println(searchSourceBuilder.toString());
        SearchHits hits = elasticsearch.search(searchRequest, RequestOptions.DEFAULT).getHits();
        SearchHit[] hits1 = hits.getHits();
        Arrays.stream(hits1).forEach(h->System.out.println(h.getSourceAsMap()));
        return hits.toString();

      /*  BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should(QueryBuilders.nestedQuery(""))
        QueryBuilders.scriptScoreQuery(QueryBuilders.boolQuery(),)
        IndexRequest indexRequest = new IndexRequest("posts")
                .id("1")
                .source("user", "kimchy",
                        "postDate", new Date(),
                        "message", "trying out Elasticsearch");
        IndexResponse indexResponse = elasticsearch.index(indexRequest, RequestOptions.DEFAULT);*/

    //    return null;
    }


    @GetMapping("/keywords/{keyword}/candidates")
    public ResponseEntity<Result<PixivSearchCandidatesResponse>> getCandidateWords(@NotBlank @PathVariable("keyword") String keyword) throws IOException, InterruptedException {
        return ResponseEntity.ok().body(new Result<>("搜索候选词获取成功", searchService.getCandidateWords(keyword)));
    }

    @GetMapping("/keywords/{keyword}/suggestions")
    public ResponseEntity<Result<List<SearchSuggestion>>> getSearchSuggestion(@NotBlank @PathVariable("keyword") String keyword) throws IOException, InterruptedException {
        return ResponseEntity.ok().body(new Result<>("搜索建议获取成功", searchService.getSearchSuggestion(keyword)));
    }

    @GetMapping("/keywords/{keyword}/pixivSuggestions")
    public ResponseEntity<Result<List<SearchSuggestion>>> getPixivSearchSuggestion(@NotBlank @PathVariable("keyword") String keyword) throws IOException, InterruptedException {
        return ResponseEntity.ok().body(new Result<>("搜索建议(来自Pixiv)获取成功", searchService.getPixivSearchSuggestion(keyword)));
    }

    @GetMapping("/keywords/{keyword}/translations")
    public ResponseEntity<Result<SearchSuggestion>> getKeywordTranslation(@NotBlank @PathVariable("keyword") String keyword) throws IOException, InterruptedException {
        return ResponseEntity.ok().body(new Result<>("搜索词翻译获取成功", searchService.getKeywordTranslation(keyword)));
    }

    @GetMapping("/illustrations")
    public ResponseEntity<Result<List<Illustration>>> search(@NotBlank @RequestParam("keyword") String keyword, @NotBlank @RequestParam("autoTranslate") boolean autoTranslate, @NotEmpty int page, @NotEmpty @Max(30) int pageSize) {
        if (autoTranslate) {
            //自动翻译
            keyword = searchService.translatedByYouDao(keyword);
            QueryBuilders.matchQuery("user", "kimchy")
                    .fuzziness(Fuzziness.AUTO)
                    .prefixLength(3)
                    .maxExpansions(10);
            //进入es获取
        }
        return null;
    }

}
