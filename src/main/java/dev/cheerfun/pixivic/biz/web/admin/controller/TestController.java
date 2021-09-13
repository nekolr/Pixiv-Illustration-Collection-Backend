package dev.cheerfun.pixivic.biz.web.admin.controller;

import dev.cheerfun.pixivic.biz.recommend.mapper.RecommendMapper;
import dev.cheerfun.pixivic.biz.recommend.service.NewArtistRecommendService;
import dev.cheerfun.pixivic.biz.recommend.service.NewIllustBookmarkRecommendService;
import dev.cheerfun.pixivic.biz.recommend.service.RecommendTaskService;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.common.TasteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/8/27 2:39 下午
 * @description TestController
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RequestMapping("/test")
public class TestController {
    private final NewIllustBookmarkRecommendService newIllustBookmarkRecommendService;
    private final NewArtistRecommendService newArtistRecommendService;
    private final RecommendMapper recommendMapper;
    private final RecommendTaskService recommendTaskService;

    @GetMapping("/recInit")
    public ResponseEntity<Result<List<String>>> getCheckEmail() throws TasteException {
        newIllustBookmarkRecommendService.recommend();
        return ResponseEntity.ok().body(new Result<>("生成推荐成功"));
    }
}
