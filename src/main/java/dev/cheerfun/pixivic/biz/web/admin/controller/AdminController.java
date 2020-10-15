package dev.cheerfun.pixivic.biz.web.admin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.biz.proxy.po.VIPProxyServer;
import dev.cheerfun.pixivic.biz.proxy.service.VIPProxyServerService;
import dev.cheerfun.pixivic.biz.web.admin.dto.IllustDTO;
import dev.cheerfun.pixivic.biz.web.admin.po.*;
import dev.cheerfun.pixivic.biz.web.admin.service.AdminService;
import dev.cheerfun.pixivic.biz.web.vip.service.VIPUserService;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/4/24 2:46 下午
 * @description AdminController
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;
    private final VIPUserService vipUserService;
    private final VIPProxyServerService vipProxyServerService;

    @GetMapping("/illusts/{illustId}")
    public ResponseEntity<Result<Illustration>> queryIllustrationById(@PathVariable Integer illustId, @RequestHeader(value = "Token") String token) throws JsonProcessingException {
        if (adminService.validateKey(token)) {
            log.info("管理员key:" + token + ",开始获取画作(" + illustId + ")详情");
            return ResponseEntity.ok().body(new Result<>("获取画作详情成功", adminService.queryIllustrationById(illustId)));
        }
        return ResponseEntity.ok().body(new Result<>("获取画作详情成功", null));
    }

    @PutMapping("/illusts/{illustId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Illustration>> updateIllustrationById(@PathVariable Integer illustId, @RequestHeader(value = "Authorization") String token, @RequestBody IllustDTO illustDTO) {
        adminService.updateIllusts(illustDTO);
        return ResponseEntity.ok().body(new Result<>("更新画作等级成功", null));
    }

    @PostMapping("/blockIllusts")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> blockIllustrationById(@RequestHeader(value = "Authorization") String token, @RequestBody List<Integer> illustIdList) {
        adminService.blockIllustrationById(illustIdList);
        return ResponseEntity.ok().body(new Result<>("封禁画作成功", true));
    }

    @GetMapping("/blockIllusts")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<List<Integer>>> queryBlockIllust(@RequestParam(required = false) Integer illustId, @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("获取封禁画作成功", adminService.queryBlockIllust(illustId)));
    }

    @DeleteMapping("/blockIllusts/{illustId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> removeIllustFromBlockIllust(@PathVariable Integer illustId, @RequestHeader(value = "Authorization") String token) {
        adminService.removeIllustFromBlockIllust(illustId);
        return ResponseEntity.ok().body(new Result<>("移除封禁画作成功", true));
    }

    @PostMapping("/blockArtists")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> blockArtistById(@RequestHeader(value = "Authorization") String token, @RequestBody List<Integer> artistIdList) {
        adminService.blockArtistById(artistIdList);
        return ResponseEntity.ok().body(new Result<>("封禁画师成功", true));
    }

    @GetMapping("/blockArtists")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<List<Integer>>> queryBlockArtist(@RequestParam(required = false) Integer artistId, @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("获取封禁画师成功", adminService.queryBlockArtist(artistId)));
    }

    @DeleteMapping("/blockArtists/{artistId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> removeArtistFromBlockArtist(@PathVariable Integer artistId, @RequestHeader(value = "Authorization") String token) {
        adminService.removeArtistFromBlockArtist(artistId);
        return ResponseEntity.ok().body(new Result<>("移除封禁画师成功", true));
    }

    @PostMapping("/collections")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<List<CollectionPO>>> queryCollection(
            @RequestBody CollectionPO collectionPO,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "30") Integer pageSize,
            @RequestParam(defaultValue = "id") String orderBy,
            @RequestParam(defaultValue = "asc") String orderByMode,
            @RequestHeader(value = "Authorization") String token) {
        Page<CollectionPO> collectionPOPage = adminService.queryCollection(collectionPO, page, pageSize, orderBy, orderByMode);
        return ResponseEntity.ok().body(new Result<>("获取画集列表成功", collectionPOPage.getTotalElements(), collectionPOPage.getContent()));
    }

    @PutMapping("/collections/{collectionId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<CollectionPO>> updateCollection(
            @PathVariable Integer collectionId,
            @RequestBody CollectionPO collectionPO,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("更新画集成功", adminService.updateCollection(collectionPO)));
    }

    @DeleteMapping("/collections/{collectionId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> deleteCollection(
            @PathVariable Integer collectionId,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("删除画集成功", adminService.deleteCollection(collectionId)));
    }

    @PostMapping("/discussions")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<List<DiscussionPO>>> queryDiscussion(
            @RequestBody DiscussionPO discussionPO,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "30") Integer pageSize,
            @RequestParam(defaultValue = "id") String orderBy,
            @RequestParam(defaultValue = "asc") String orderByMode,
            @RequestHeader(value = "Authorization") String token) {
        Page<DiscussionPO> discussionPOPage = adminService.queryDiscussion(discussionPO, page, pageSize, orderBy, orderByMode);
        return ResponseEntity.ok().body(new Result<>("获取讨论列表成功", discussionPOPage.getTotalElements(), discussionPOPage.getContent()));
    }

    @PutMapping("/discussions/{discussionId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<DiscussionPO>> updateDiscussion(
            @PathVariable Integer discussionId,
            @RequestBody DiscussionPO discussionPO,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("更新讨论成功", adminService.updateDiscussion(discussionPO)));
    }

    @DeleteMapping("/discussions/{discussionId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> deleteDiscussion(
            @PathVariable Integer discussionId,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("删除讨论成功", adminService.deleteDiscussion(discussionId)));
    }

    @PostMapping("/sections")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<List<SectionPO>>> querySection(
            @RequestBody SectionPO sectionPO,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "30") Integer pageSize,
            @RequestParam(defaultValue = "id") String orderBy,
            @RequestParam(defaultValue = "asc") String orderByMode,
            @RequestHeader(value = "Authorization") String token) {
        Page<SectionPO> sectionPOPage = adminService.querySection(sectionPO, page, pageSize, orderBy, orderByMode);
        return ResponseEntity.ok().body(new Result<>("获取板块列表成功", sectionPOPage.getTotalElements(), sectionPOPage.getContent()));
    }

    @PutMapping("/sections/{sectionId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<SectionPO>> updateSection(
            @PathVariable Integer sectionId,
            @RequestBody SectionPO sectionPO,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("更新板块成功", adminService.updateSection(sectionPO)));
    }

    @DeleteMapping("/sections/{sectionId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> deleteSection(
            @PathVariable Integer sectionId,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("删除板块成功", adminService.deleteSection(sectionId)));
    }

    @PostMapping("/users")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<List<UserPO>>> queryUsers(
            @RequestBody UserPO userPO,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "30") Integer pageSize,
            @RequestParam(defaultValue = "id") String orderBy,
            @RequestParam(defaultValue = "asc") String orderByMode,
            @RequestHeader(value = "Authorization") String token) {
        Page<UserPO> userPOPage = adminService.queryUsers(userPO, page, pageSize, orderBy, orderByMode);
        return ResponseEntity.ok().body(new Result<>("获取用户列表成功", userPOPage.getTotalElements(), userPOPage.getContent()));
    }

    @PostMapping("/banUsers/{userId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<UserPO>> banUser(
            @PathVariable Integer userId,
            @RequestBody UserPO userPO,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("封禁用户成功", adminService.updateUser(userPO)));
    }

    @DeleteMapping("/banUsers/{userId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<UserPO>> releaseUser(
            @PathVariable Integer userId,
            @RequestBody UserPO userPO,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("解封用户成功", adminService.updateUser(userPO)));
    }

    @DeleteMapping("/users/{userId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> deleteUser(
            @PathVariable Integer userId,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("删除用户成功", adminService.deleteUser(userId)));
    }

    @PostMapping("/comments")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<List<CommentPO>>> queryComments(
            @RequestBody CommentPO commentPO,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "30") Integer pageSize,
            @RequestParam(defaultValue = "id") String orderBy,
            @RequestParam(defaultValue = "asc") String orderByMode,
            @RequestHeader(value = "Authorization") String token) {
        Page<CommentPO> commentPOPage = adminService.queryComment(commentPO, page, pageSize, orderBy, orderByMode);
        return ResponseEntity.ok().body(new Result<>("获取评论列表成功", commentPOPage.getTotalElements(), commentPOPage.getContent()));
    }

    @PutMapping("/comments/{commentId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<CommentPO>> updateComment(
            @PathVariable Integer commentId,
            @RequestBody CommentPO commentPO,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("更新评论成功", adminService.updateComment(commentPO)));
    }

    @DeleteMapping("/comments/{commentId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> deleteComment(
            @PathVariable Integer commentId,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("删除评论成功", adminService.deleteComment(adminService.queryCommentById(commentId))));
    }

    @PostMapping("/announcements")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<List<AnnouncementPO>>> queryAnnouncements(
            @RequestBody AnnouncementPO announcementPO,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "30") Integer pageSize,
            @RequestParam(defaultValue = "id") String orderBy,
            @RequestParam(defaultValue = "asc") String orderByMode,
            @RequestHeader(value = "Authorization") String token) {
        Page<AnnouncementPO> announcementPOS = adminService.queryAnnouncement(announcementPO, page, pageSize, orderBy, orderByMode);
        return ResponseEntity.ok().body(new Result<>("获取公告列表成功", announcementPOS.getTotalElements(), announcementPOS.getContent()));
    }

    @PutMapping("/announcements/{announcementId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<AnnouncementPO>> updateAnnouncement(
            @PathVariable Integer announcementId,
            @RequestBody AnnouncementPO announcementPO,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("更新公告成功", adminService.updateAnnouncement(announcementPO)));
    }

    @PutMapping("/announcements")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<AnnouncementPO>> createAnnouncement(
            @RequestBody AnnouncementPO announcementPO,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("发布公告成功", adminService.createAnnouncement(announcementPO)));
    }

    @DeleteMapping("/announcements/{announcementId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> deleteAnnouncement(
            @PathVariable Integer announcementId,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("删除公告成功", adminService.deleteAnnouncement(announcementId)));
    }

    @DeleteMapping("/caches/{region}/{key}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> deleteCache(
            @PathVariable String region,
            @PathVariable String key,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("删除缓存成功", adminService.deleteCache(region, key)));
    }

    @DeleteMapping("/caches/{region}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> deleteAllCache(
            @PathVariable String region,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("删除缓存成功", adminService.deleteCache(region, null)));
    }

    @DeleteMapping("/caches")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> deleteAllCache(
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("删除缓存成功", adminService.deleteCache(null, null)));
    }

//    @PutMapping("/illusts")
//    public ResponseEntity<Result<User>> updateIllusts(@RequestBody @Valid IllustDTO illustDTO, @RequestHeader(value = "Authorization") String token) {
//        adminService.updateIllusts(illustDTO);
//        return null;
//    }

    //广告管理
    @PostMapping("/ads")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<List<AdvertisementPO>>> queryAdvertisements(
            @RequestBody AdvertisementPO advertisementPO,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "30") Integer pageSize,
            @RequestParam(defaultValue = "id") String orderBy,
            @RequestParam(defaultValue = "asc") String orderByMode,
            @RequestHeader(value = "Authorization") String token) {
        Page<AdvertisementPO> advertisementPOS = adminService.queryAdvertisements(advertisementPO, page, pageSize, orderBy, orderByMode);
        return ResponseEntity.ok().body(new Result<>("获取广告列表成功", advertisementPOS.getTotalElements(), advertisementPOS.getContent()));
    }

    @PutMapping("/ads/{adId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<AdvertisementPO>> updateAdvertisement(
            @PathVariable Integer adId,
            @RequestBody AdvertisementPO advertisementPO,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("更新广告成功", adminService.updateAdvertisement(advertisementPO)));
    }

    @PutMapping("/ads")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<AdvertisementPO>> createAdvertisement(
            @RequestBody AdvertisementPO advertisementPO,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("发布广告成功", adminService.createAdvertisement(advertisementPO)));
    }

    @DeleteMapping("/ads/{adId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> deleteAdvertisement(
            @PathVariable Integer adId,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("删除广告成功", adminService.deleteAdvertisement(adId)));
    }

    //app版本管理

    //推荐管理
    @DeleteMapping("/recommendation/viewIllust")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> deleteViewIllustRecommendation(
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("删除可能想看推荐成功", adminService.deleteViewIllustRecommendation()));
    }

    //生成兑换码
    @GetMapping("/exchangeCode")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<List<String>>> getCheckEmail(@RequestParam byte type, @RequestParam Integer sum, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("生成兑换码成功", vipUserService.generateExchangeCode(type, sum)));
    }

    //添加高速服务器
    @PostMapping("/vipProxyServer")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result> addVipProxyServer(@RequestParam String url, @RequestHeader("Authorization") String token) {
        vipProxyServerService.addServer(url);
        return ResponseEntity.ok().body(new Result<>("添加高速服务器成功"));
    }

    //获取高速服务器
    @GetMapping("/vipProxyServer")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<List<VIPProxyServer>>> queryVipProxyServer(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("获取高速服务器成功", vipProxyServerService.queryAllServer()));
    }

}
