package dev.cheerfun.pixivic.biz.web.user.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.biz.userInfo.annotation.WithUserInfo;
import dev.cheerfun.pixivic.biz.web.collection.po.Collection;
import dev.cheerfun.pixivic.biz.web.user.dto.ArtistWithRecentlyIllusts;
import dev.cheerfun.pixivic.biz.web.user.po.BookmarkCollectionRelation;
import dev.cheerfun.pixivic.biz.web.user.po.BookmarkRelation;
import dev.cheerfun.pixivic.biz.web.user.po.FollowedRelation;
import dev.cheerfun.pixivic.biz.web.user.po.FollowedUserRelation;
import dev.cheerfun.pixivic.biz.web.user.service.BusinessService;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Artist;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Result;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/14 12:08
 * @description BizController
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@PermissionRequired
@RequestMapping("/users")
public class BusinessController {
    private final BusinessService businessService;

    @PostMapping("/bookmarked")
    public ResponseEntity<Result<String>> bookmark(@RequestBody @Valid BookmarkRelation bookmarkRelation, @RequestHeader("Authorization") String token) {
        businessService.bookmark((int) AppContext.get().get(AuthConstant.USER_ID), bookmarkRelation.getUsername(), bookmarkRelation.getIllustId());
        return ResponseEntity.ok().body(new Result<>("收藏成功"));
    }

    @DeleteMapping("/bookmarked")
    public ResponseEntity<Result<String>> cancelBookmark(@RequestBody BookmarkRelation bookmarkRelation, @RequestHeader("Authorization") String token) {
        businessService.cancelBookmark((int) AppContext.get().get(AuthConstant.USER_ID), bookmarkRelation.getIllustId(), bookmarkRelation.getId());
        return ResponseEntity.ok().body(new Result<>("取消收藏成功"));
    }

    @GetMapping("/{userId}/bookmarked/{type}")
    @PermissionRequired(PermissionLevel.ANONYMOUS)
    @WithUserInfo
    public ResponseEntity<Result<List<Illustration>>> queryBookmark(@PathVariable Integer userId, @PathVariable String type, @RequestParam(defaultValue = "1") @Max(300) int page, @RequestParam(defaultValue = "30") @Max(30) int pageSize, @RequestHeader(value = "Authorization", required = false) String token) {
        List<Illustration> illustrations = businessService.queryBookmarked(userId, type, (page - 1) * pageSize, pageSize);
       /* int userIdFromAppContext;
        if (token != null) {
            userIdFromAppContext = (int) AppContext.get().get(AuthConstant.USER_ID);
            businessService.dealIfFollowedInfo(illustrations, userIdFromAppContext);
        }*/
        return ResponseEntity.ok().body(new Result<>("获取收藏画作成功", illustrations));
    }

    @GetMapping("/{userId}/{illustId}/isBookmarked")
    public ResponseEntity<Result<Boolean>> queryIsBookmarked(@PathVariable Integer userId, @PathVariable Integer illustId, @RequestHeader("Authorization") String token) {
        Boolean isBookmark = businessService.queryIsBookmarked((int) AppContext.get().get(AuthConstant.USER_ID), illustId);
        return ResponseEntity.ok().body(new Result<>("获取是否收藏画作成功", isBookmark));
    }

    @PostMapping("/followed")
    public ResponseEntity<Result<String>> follow(@RequestBody @Valid FollowedRelation followedRelation, @RequestHeader("Authorization") String token) {
        businessService.follow((int) AppContext.get().get(AuthConstant.USER_ID), followedRelation.getArtistId(), followedRelation.getUsername());
        return ResponseEntity.ok().body(new Result<>("follow成功"));
    }

    @DeleteMapping("/followed")
    public ResponseEntity<Result<String>> cancelFollow(@RequestBody FollowedRelation followedRelation, @RequestHeader("Authorization") String token) {
        businessService.cancelFollow((int) AppContext.get().get(AuthConstant.USER_ID), followedRelation.getArtistId());
        return ResponseEntity.ok().body(new Result<>("取消follow成功"));
    }

    @GetMapping("/{userId}/followed")
    @PermissionRequired(PermissionLevel.ANONYMOUS)
    public ResponseEntity<Result<List<Artist>>> queryFollowed(@PathVariable Integer userId, @RequestParam(defaultValue = "1") @Max(100) int page, @RequestParam(defaultValue = "30") @Max(30) int pageSize, @RequestHeader(value = "Authorization", required = false) String token) {
        List<Artist> artists = businessService.queryFollowed(userId, (page - 1) * pageSize, pageSize);
        return ResponseEntity.ok().body(new Result<>("获取follow画师列表成功", artists));
    }

    @GetMapping("/{userId}/followedWithRecentlyIllusts")
    @PermissionRequired(PermissionLevel.ANONYMOUS)
    @WithUserInfo
    public ResponseEntity<Result<List<ArtistWithRecentlyIllusts>>> queryFollowedWithRecentlyIllusts(@PathVariable Integer userId, @RequestParam(defaultValue = "1") @Max(100) int page, @RequestParam(defaultValue = "30") @Max(30) int pageSize, @RequestHeader(value = "Authorization", required = false) String token) {
        List<ArtistWithRecentlyIllusts> artists = businessService.queryFollowedWithRecentlyIllusts(userId, (page - 1) * pageSize, pageSize);
        return ResponseEntity.ok().body(new Result<>("获取带有近期画作的follow画师列表成功", artists));
    }

    @GetMapping("/{userId}/followed/latest/{type}")
    @WithUserInfo
    public ResponseEntity<Result<List<Illustration>>> queryFollowedLatest(@PathVariable String userId, @PathVariable String type, @RequestParam(defaultValue = "1") @Max(150) int page, @RequestParam(defaultValue = "30") @Max(30) int pageSize, @RequestHeader("Authorization") String token) {
        List<Illustration> illustrationList = businessService.queryFollowedLatest((int) AppContext.get().get(AuthConstant.USER_ID), type, page, pageSize);
        return ResponseEntity.ok().body(new Result<>("获取follow画师最新画作成功", illustrationList));
    }

    @GetMapping("/{userId}/{artistId}/isFollowed")
    public ResponseEntity<Result<Boolean>> queryIsFollowed(@PathVariable String userId, @PathVariable Integer artistId, @RequestHeader("Authorization") String token) {
        Boolean isFollowed = businessService.queryIsFollowed((int) AppContext.get().get(AuthConstant.USER_ID), artistId);
        return ResponseEntity.ok().body(new Result<>("获取是否follow画师成功", isFollowed));
    }

    @GetMapping("/{userId}/actionHistory")
    public ResponseEntity<Result<String>> queryActionHistory(@PathVariable String userId, @RequestHeader("Authorization") String token) {
        //查看行为历史
        return ResponseEntity.ok().body(new Result<>("获取行为历史成功"));
    }

    @PostMapping("/{illustId}/tags")
    public ResponseEntity<Result<String>> addTag(@PathVariable String illustId, @RequestHeader("Authorization") String token, @RequestBody List<Tag> tags) {
        businessService.addTag((int) AppContext.get().get(AuthConstant.USER_ID), illustId, tags);
        return ResponseEntity.ok().body(new Result<>("成功为画作添加标签"));
    }

    @PostMapping("/bookmarked/collections")
    public ResponseEntity<Result<String>> bookmarkCollection(@RequestBody @Valid BookmarkCollectionRelation bookmarkCollectionRelation, @RequestHeader("Authorization") String token) {
        businessService.bookmarkCollection((int) AppContext.get().get(AuthConstant.USER_ID), bookmarkCollectionRelation.getUsername(), bookmarkCollectionRelation.getCollectionId());
        return ResponseEntity.ok().body(new Result<>("收藏画集成功"));
    }

    @DeleteMapping("/bookmarked/collections")
    public ResponseEntity<Result<String>> cancelBookmarkCollection(@RequestBody BookmarkCollectionRelation bookmarkCollectionRelation, @RequestHeader("Authorization") String token) {
        businessService.cancelBookmarkCollection((int) AppContext.get().get(AuthConstant.USER_ID), bookmarkCollectionRelation.getCollectionId());
        return ResponseEntity.ok().body(new Result<>("取消收藏画集成功"));
    }

    @GetMapping("/{userId}/bookmarked/collections")
    public ResponseEntity<Result<List<Collection>>> queryBookmarkCollection(@PathVariable Integer userId, @RequestParam(defaultValue = "1") @Max(100) int page, @RequestParam(defaultValue = "30") @Max(30) int pageSize, @RequestHeader("Authorization") String token) {
        List<Collection> collections = businessService.queryBookmarkCollection(userId, page, pageSize);
        return ResponseEntity.ok().body(new Result<>("获取收藏画集成功", businessService.queryUserTotalBookmarkCollection(userId), collections));
    }

    @PostMapping("/liked/collections")
    public ResponseEntity<Result<String>> likeCollection(@RequestBody BookmarkCollectionRelation bookmarkCollectionRelation, @RequestHeader("Authorization") String token) {
        businessService.likeCollection((int) AppContext.get().get(AuthConstant.USER_ID), bookmarkCollectionRelation.getCollectionId());
        return ResponseEntity.ok().body(new Result<>("点赞画集成功"));
    }

    @DeleteMapping("/liked/collections")
    public ResponseEntity<Result<String>> cancelLikeCollection(@RequestBody BookmarkCollectionRelation bookmarkCollectionRelation, @RequestHeader("Authorization") String token) {
        businessService.cancelLikeCollection((int) AppContext.get().get(AuthConstant.USER_ID), bookmarkCollectionRelation.getCollectionId());
        return ResponseEntity.ok().body(new Result<>("取消点赞画集成功"));
    }

    @PostMapping("/followed/users")
    public ResponseEntity<Result<String>> followUser(@RequestBody @Valid FollowedUserRelation followedUserRelation, @RequestHeader("Authorization") String token) {
        businessService.followUser((int) AppContext.get().get(AuthConstant.USER_ID), followedUserRelation.getUsername(), followedUserRelation.getFollowedUserId());
        return ResponseEntity.ok().body(new Result<>("关注用户成功"));
    }

    @DeleteMapping("/followed/users")
    public ResponseEntity<Result<String>> cancelFollowUser(@RequestBody FollowedUserRelation followedUserRelation, @RequestHeader("Authorization") String token) {
        businessService.cancelFollowUser((int) AppContext.get().get(AuthConstant.USER_ID), followedUserRelation.getFollowedUserId());
        return ResponseEntity.ok().body(new Result<>("取消关注用户成功"));
    }

}
