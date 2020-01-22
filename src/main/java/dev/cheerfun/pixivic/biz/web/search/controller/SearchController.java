package dev.cheerfun.pixivic.biz.web.search.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.basic.sensitive.annotation.SensitiveCheck;
import dev.cheerfun.pixivic.biz.web.dto.IllustrationWithLikeInfo;
import dev.cheerfun.pixivic.biz.web.search.domain.SearchResult;
import dev.cheerfun.pixivic.biz.web.search.domain.SearchSuggestion;
import dev.cheerfun.pixivic.biz.web.search.domain.response.PixivSearchCandidatesResponse;
import dev.cheerfun.pixivic.biz.web.search.service.SearchService;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
    public CompletableFuture<ResponseEntity<Result<PixivSearchCandidatesResponse>>> getCandidateWords(HttpServletRequest request) {
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
    @PermissionRequired(PermissionLevel.ANONYMOUS)
    public CompletableFuture<ResponseEntity<Result<SearchResult>>> searchByKeyword(
            @SensitiveCheck
            @RequestParam
            @NotBlank
                    String keyword,
            @RequestParam(defaultValue = "30") @Validated
            @NonNull @Max(1000) @Min(1)
                    int pageSize,
            @RequestParam @Validated
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
                    int maxSanityLevel, @RequestHeader(value = "Authorization", required = false) String token) {
        if ("autoTranslate".equals(searchType)) {
            //自动翻译
            String[] keywords = keyword.split(" ");
            keyword = Arrays.stream(keywords).map(searchService::translatedByYouDao).reduce((s1, s2) -> s1 + " " + s2).get();
        }
        CompletableFuture<SearchResult> searchResultCompletableFuture = searchService.searchByKeyword(keyword, pageSize, page, searchType, illustType, minWidth, minHeight, beginDate, endDate, xRestrict, popWeight, minTotalBookmarks, minTotalView, maxSanityLevel);
        return searchResultCompletableFuture.thenApply(illustrations -> ResponseEntity.ok().body(new Result<>("搜索结果获取成功", illustrations)));
    }

    @GetMapping("/similarityImages")
    public CompletableFuture<ResponseEntity<Result<List<Illustration>>>> searchByImage(@RequestParam String imageUrl) {
        return searchService.searchByImage(imageUrl).thenApply(saucenaoResponse -> ResponseEntity.ok().body(new Result<>("搜索结果获取成功", saucenaoResponse)));

    }

}
