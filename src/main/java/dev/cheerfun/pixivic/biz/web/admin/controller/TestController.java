package dev.cheerfun.pixivic.biz.web.admin.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.biz.recommend.service.NewIllustBookmarkRecommendService;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/recInit")
    public ResponseEntity<Result<List<String>>> getCheckEmail(@RequestParam Integer userId) {
        newIllustBookmarkRecommendService.dealPerUser(List.of(userId), 3000);
        return ResponseEntity.ok().body(new Result<>("生成推荐成功"));
    }
}
