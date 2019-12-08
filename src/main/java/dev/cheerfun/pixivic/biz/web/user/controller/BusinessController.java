package dev.cheerfun.pixivic.biz.web.user.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.biz.web.user.po.BookmarkRelation;
import dev.cheerfun.pixivic.biz.web.user.po.FollowedRelation;
import dev.cheerfun.pixivic.biz.web.user.service.BusinessService;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Artist;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Result;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/14 12:08
 * @description BizController
 */
@RestController
@PermissionRequired
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BusinessController {
    private final BusinessService businessService;
    private static final String USER_ID = "userId";

    @PostMapping("/bookmarked")
    public ResponseEntity<Result<String>> bookmark(@RequestBody BookmarkRelation bookmarkRelation, @RequestHeader("Authorization") String token) {
        businessService.bookmark((int) AppContext.get().get(USER_ID), bookmarkRelation.getIllustId());
        return ResponseEntity.ok().body(new Result<>("收藏成功"));
    }

    @DeleteMapping("/bookmarked")
    public ResponseEntity<Result<String>> cancelBookmark(@RequestBody BookmarkRelation bookmarkRelation, @RequestHeader("Authorization") String token) {
        businessService.cancelBookmark((int) AppContext.get().get(USER_ID), bookmarkRelation.getIllustId());
        return ResponseEntity.ok().body(new Result<>("取消收藏成功"));
    }

    @GetMapping("/{userId}/bookmarked/{type}")
    public ResponseEntity<Result<List<Illustration>>> queryBookmark(@PathVariable String userId, @PathVariable String type, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "30") int pageSize, @RequestHeader("Authorization") String token) {
        List<Illustration> illustrations = businessService.queryBookmarked((int) AppContext.get().get(USER_ID), type, (page - 1) * pageSize, pageSize);
        return ResponseEntity.ok().body(new Result<>("获取收藏画作成功", illustrations));
    }

    @GetMapping("/{userId}/{illustId}/isBookmarked")
    public ResponseEntity<Result<Boolean>> queryIsBookmarked(@PathVariable String userId, @PathVariable String illustId, @RequestHeader("Authorization") String token) {
        Boolean isBookmark = businessService.queryIsBookmarked((int) AppContext.get().get(USER_ID), illustId);
        return ResponseEntity.ok().body(new Result<>("获取是否收藏画作成功", isBookmark));
    }

    @PostMapping("/followed")
    public ResponseEntity<Result<String>> follow(@RequestBody FollowedRelation followedRelation, @RequestHeader("Authorization") String token) {
        businessService.follow((int) AppContext.get().get(USER_ID), followedRelation.getArtistId());
        return ResponseEntity.ok().body(new Result<>("follow成功"));
    }

    @DeleteMapping("/followed")
    public ResponseEntity<Result<String>> cancelFollow(@RequestBody FollowedRelation followedRelation, @RequestHeader("Authorization") String token) {
        businessService.cancelFollow((int) AppContext.get().get(USER_ID), followedRelation.getArtistId());
        return ResponseEntity.ok().body(new Result<>("取消follow成功"));
    }

    @GetMapping("/{userId}/followed")
    public ResponseEntity<Result<List<Artist>>> queryFollowed(@PathVariable String userId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "30") int pageSize, @RequestHeader("Authorization") String token) {
        List<Artist> artists = businessService.queryFollowed((int) AppContext.get().get(USER_ID), (page - 1) * pageSize, pageSize);
        return ResponseEntity.ok().body(new Result<>("获取follow画师列表成功", artists));
    }

    @GetMapping("/{userId}/followed/latest/{type}")
    public ResponseEntity<Result<List<Illustration>>> queryFollowedLatest(@PathVariable String userId, @PathVariable String type, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "30") int pageSize, @RequestHeader("Authorization") String token) {
        List<Illustration> illustrationList = businessService.queryFollowedLatest((int) AppContext.get().get(USER_ID), type, (page - 1) * pageSize, pageSize);
        return ResponseEntity.ok().body(new Result<>("获取follow画师最新画作成功", illustrationList));
    }

    @GetMapping("/{userId}/{artistId}/isFollowed")
    public ResponseEntity<Result<Boolean>> queryIsFollowed(@PathVariable String userId, @PathVariable String artistId, @RequestHeader("Authorization") String token) {
        Boolean isFollowed = businessService.queryIsFollowed((int) AppContext.get().get(USER_ID), artistId);
        return ResponseEntity.ok().body(new Result<>("获取是否follow画师成功", isFollowed));
    }

    @GetMapping("/users/{userId}/actionHistory")
    public ResponseEntity<Result<String>> queryActionHistory(@PathVariable String userId, @RequestHeader("Authorization") String token) {
        //查看行为历史
        return ResponseEntity.ok().body(new Result<>("获取行为历史成功"));
    }

    @PostMapping("/{illustId}/tags")
    public ResponseEntity<Result<String>> addTag(@PathVariable String illustId, @RequestHeader("Authorization") String token, @RequestBody List<Tag> tags) {
        businessService.addTag((int) AppContext.get().get(USER_ID), illustId, tags);
        return ResponseEntity.ok().body(new Result<>("成功为画作添加标签"));
    }

}
