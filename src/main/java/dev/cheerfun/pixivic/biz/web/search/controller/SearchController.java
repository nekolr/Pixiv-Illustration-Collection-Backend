package dev.cheerfun.pixivic.biz.web.search.controller;

import dev.cheerfun.pixivic.biz.web.search.model.Response.PixivSearchCandidatesResponse;
import dev.cheerfun.pixivic.biz.web.search.model.Response.SaucenaoResponse;
import dev.cheerfun.pixivic.biz.web.search.model.SearchResult;
import dev.cheerfun.pixivic.biz.web.search.model.SearchSuggestion;
import dev.cheerfun.pixivic.biz.web.search.service.SearchService;
import dev.cheerfun.pixivic.common.model.Illustration;
import dev.cheerfun.pixivic.common.model.Result;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/14 22:45
 * @description SearchController
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
//@PermissionRequired
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/keywords/**/candidates")
    public CompletableFuture<ResponseEntity<Result<PixivSearchCandidatesResponse>>> getCandidateWords(HttpServletRequest request) throws IOException, InterruptedException {
        return searchService.getCandidateWords(searchService.getKeyword(request)).thenApply(r -> ResponseEntity.ok().body(new Result<>("搜索候选词获取成功", r)));
    }

    @GetMapping("/keywords/**/suggestions")
    public CompletableFuture<ResponseEntity<Result<List<SearchSuggestion>>>> getSearchSuggestion(HttpServletRequest request) throws IOException, InterruptedException {
        return searchService.getSearchSuggestion(searchService.getKeyword(request)).thenApply(r -> ResponseEntity.ok().body(new Result<>("搜索建议获取成功", r)));
    }

    @GetMapping("/keywords/**/pixivSuggestions")
    public CompletableFuture<ResponseEntity<Result<List<SearchSuggestion>>>> getPixivSearchSuggestion(HttpServletRequest request) {
        return searchService.getPixivSearchSuggestion(searchService.getKeyword(request)).thenApply(r -> ResponseEntity.ok().body(new Result<>("搜索建议(来自Pixiv)获取成功", r)));
    }

    @GetMapping("/keywords/**/translations")
    public ResponseEntity<Result<SearchSuggestion>> getKeywordTranslation(HttpServletRequest request) {
        return ResponseEntity.ok().body(new Result<>("搜索词翻译获取成功", searchService.getKeywordTranslation(searchService.getKeyword(request))));
    }

    @GetMapping("/illustrations")
    public CompletableFuture<ResponseEntity<Result<SearchResult>>> searchByKeyword(
            @RequestParam
            @NotBlank
                    String keyword,
            @RequestParam(defaultValue = "30")
            @NonNull @Max(60) @Min(1)
                    int pageSize,
            @RequestParam
            @NonNull @Max(1600) @Min(1)
                    int page,
            @RequestParam(defaultValue = "original")
                    String searchType,//搜索类型（原生、自动翻译、自动匹配词条）
            @RequestParam(defaultValue = "illust")
                    String illustType,
            @RequestParam(defaultValue = "0")
                    int minWidth,
            @RequestParam(defaultValue = "0")
                    int minHeight,
            @RequestParam(defaultValue = "2008-01-01")
                    String beginDate,
            @RequestParam(defaultValue = "9999-12-31")
                    String endDate,
            @RequestParam(defaultValue = "0")
                    int xRestrict,
            @RequestParam(defaultValue = "0")
                    int popWeight,
            @RequestParam(defaultValue = "0")
                    int minTotalBookmarks,
            @RequestParam(defaultValue = "0")
                    int minTotalView,
            @RequestParam(defaultValue = "5")
                    int maxSanityLevel) {
        if ("autoTranslate".equals(searchType)) {
            //自动翻译
            String[] keywords = keyword.split(" ");
            keyword = Arrays.stream(keywords).map(searchService::translatedByYouDao).reduce((s1, s2) -> s1 + " " + s2).get();
        }
        return searchService.searchByKeyword(keyword, pageSize, page, searchType, illustType, minWidth, minHeight, beginDate, endDate, xRestrict, popWeight, minTotalBookmarks, minTotalView, maxSanityLevel).thenApply(illustrations -> ResponseEntity.ok().body(new Result<>("搜索结果获取成功", illustrations)));
    }

    //TODO 存在画作数据时查询并返回画作信息
    @GetMapping("/similarityImages")
    public CompletableFuture<ResponseEntity<Result<SaucenaoResponse>>> searchByImage(@RequestParam String imageUrl) {
        return searchService.searchByImage(imageUrl).thenApply(saucenaoResponse -> ResponseEntity.ok().body(new Result<>("搜索结果获取成功", saucenaoResponse)));

    }

}
