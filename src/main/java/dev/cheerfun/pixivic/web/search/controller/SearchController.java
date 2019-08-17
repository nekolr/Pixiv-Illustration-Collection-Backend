package dev.cheerfun.pixivic.web.search.controller;

import dev.cheerfun.pixivic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.common.model.Illustration;
import dev.cheerfun.pixivic.common.model.Result;
import dev.cheerfun.pixivic.web.search.model.Response.PixivSearchCandidatesResponse;
import dev.cheerfun.pixivic.web.search.model.SearchSuggestion;
import dev.cheerfun.pixivic.web.search.service.SearchService;
import lombok.RequiredArgsConstructor;
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
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/14 22:45
 * @description SearchController
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
@PermissionRequired
public class SearchController {
    private final SearchService searchService;

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
        }
        return null;
    }
}
