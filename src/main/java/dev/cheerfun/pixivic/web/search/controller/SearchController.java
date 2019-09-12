package dev.cheerfun.pixivic.web.search.controller;

import dev.cheerfun.pixivic.common.model.Illustration;
import dev.cheerfun.pixivic.common.model.Result;
import dev.cheerfun.pixivic.web.search.model.Response.PixivSearchCandidatesResponse;
import dev.cheerfun.pixivic.web.search.model.SearchRequest;
import dev.cheerfun.pixivic.web.search.model.SearchSuggestion;
import dev.cheerfun.pixivic.web.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/keywords/{keyword}/candidates")
    public ResponseEntity<Result<PixivSearchCandidatesResponse>> getCandidateWords(@Validated @NotBlank @PathVariable("keyword") String keyword) throws IOException, InterruptedException {
        return ResponseEntity.ok().body(new Result<>("搜索候选词获取成功", searchService.getCandidateWords(keyword)));
    }

    @GetMapping("/keywords/{keyword}/suggestions")
    public ResponseEntity<Result<List<SearchSuggestion>>> getSearchSuggestion(@Validated @NotBlank @PathVariable("keyword") String keyword) throws IOException, InterruptedException {
        return ResponseEntity.ok().body(new Result<>("搜索建议获取成功", searchService.getSearchSuggestion(keyword)));
    }

    @GetMapping("/keywords/{keyword}/pixivSuggestions")
    public ResponseEntity<Result<List<SearchSuggestion>>> getPixivSearchSuggestion(@Validated @NotBlank @PathVariable("keyword") String keyword) throws IOException, InterruptedException {
        return ResponseEntity.ok().body(new Result<>("搜索建议(来自Pixiv)获取成功", searchService.getPixivSearchSuggestion(keyword)));
    }

    @GetMapping("/keywords/{keyword}/translations")
    public ResponseEntity<Result<SearchSuggestion>> getKeywordTranslation(@Validated @NotBlank @PathVariable("keyword") String keyword) throws IOException, InterruptedException {
        return ResponseEntity.ok().body(new Result<>("搜索词翻译获取成功", searchService.getKeywordTranslation(keyword)));
    }

    @GetMapping("/illustrations")
    public CompletableFuture<ResponseEntity<Result<List<Illustration>>>> searchByKeyword(@Validated @RequestBody SearchRequest searchRequest) {
        if ("autoTranslate".equals(searchRequest.getSearchType())) {
            //自动翻译
            String[] keywords = searchRequest.getKeyword().split(" ");
            searchRequest.setKeyword(Arrays.stream(keywords).map(searchService::translatedByYouDao).reduce((s1, s2) -> s1 + " " + s2).get());
        }
        return searchService.searchByKeyword(searchRequest).thenApply(illustrations -> ResponseEntity.ok().body(new Result<>("搜索结果获取成功", illustrations)));
    }

    @GetMapping("/images")
    public CompletableFuture<ResponseEntity<Result<List<Illustration>>>> searchByImage(@RequestParam String imageUrl) {
        searchService.searchByImage(imageUrl);
        return null;
    }
}
