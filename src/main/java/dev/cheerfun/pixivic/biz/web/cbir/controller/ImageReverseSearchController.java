package dev.cheerfun.pixivic.biz.web.cbir.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.biz.web.cbir.service.ImageReverseSearchService;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/5/15 7:39 PM
 * @description ImageReverseSearchController
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ImageReverseSearchController {
    private final ImageReverseSearchService imageReverseSearchService;

    @GetMapping("/similarImages")
    public ResponseEntity<Result<List<Illustration>>> queryTopKSimilarImage(@RequestParam("imageUrl") String imageUrl, @RequestHeader(value = AuthConstant.AUTHORIZATION, required = false) String token) throws IOException, InterruptedException {
        return ResponseEntity.ok().body(new Result<>("获取以图搜图结果成功", imageReverseSearchService.queryTopKSimilarImage(imageUrl)));
    }

    @GetMapping("/imageTags")
    @PermissionRequired
    public ResponseEntity<Result<List<String>>> generateTagForImage(@RequestParam("imageUrl") String imageUrl, @RequestHeader(value = AuthConstant.AUTHORIZATION, required = false) String token) throws IOException, InterruptedException {
        return ResponseEntity.ok().body(new Result<>("获取图片相关标签结果成功", imageReverseSearchService.generateTagForImage(imageUrl)));
    }
}
