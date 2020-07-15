package dev.cheerfun.pixivic.biz.web.recommend.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.biz.userInfo.annotation.WithUserInfo;
import dev.cheerfun.pixivic.biz.web.recommend.service.RecommendBizService;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Artist;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/7/8 2:08 下午
 * @description RecommondController
 */
@RestController
@PermissionRequired
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RecommendBizController {
    private final RecommendBizService recommendBizService;

    //获取可能喜欢的画作
    @WithUserInfo
    @GetMapping("/users/{userId}/recommendBookmarkIllusts")
    public ResponseEntity<Result<List<Illustration>>> recommendBookmarkIllusts(@PathVariable Integer userId, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "30") Integer pageSize, @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok().body(new Result<>("获取可能喜欢的画作成功", recommendBizService.queryRecommendBookmarkIllust((Integer) AppContext.get().get(AuthConstant.USER_ID), page, pageSize)));
    }

    //获取可能想看的画作
    @WithUserInfo
    @GetMapping("/users/{userId}/recommendViewIllusts")
    public ResponseEntity<Result<List<Illustration>>> recommendViewIllusts(@PathVariable Integer userId, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "30") Integer pageSize, @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok().body(new Result<>("获取可能想看的画作成功", recommendBizService.queryRecommendViewIllust((Integer) AppContext.get().get(AuthConstant.USER_ID), page, pageSize)));
    }

    //获取可能喜欢的画师
    @WithUserInfo
    @GetMapping("/users/{userId}/recommendArtists")
    public ResponseEntity<Result<List<Artist>>> recommendArtists(@PathVariable Integer userId, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "30") Integer pageSize, @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok().body(new Result<>("获取推荐画师成功", recommendBizService.queryRecommendArtist((Integer) AppContext.get().get(AuthConstant.USER_ID), page, pageSize)));
    }

}
