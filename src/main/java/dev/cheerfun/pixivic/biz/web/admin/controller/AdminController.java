package dev.cheerfun.pixivic.biz.web.admin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.biz.web.admin.dto.IllustDTO;
import dev.cheerfun.pixivic.biz.web.admin.po.*;
import dev.cheerfun.pixivic.biz.web.admin.service.AdminService;
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

    @GetMapping("/illusts/{illustId}")
    public ResponseEntity<Result<Illustration>> queryIllustrationById(@PathVariable Integer illustId, @RequestHeader(value = "Token", required = false) String token) throws JsonProcessingException {
        if (adminService.validateKey(token)) {
            log.info("管理员key:" + token + ",开始获取画作(" + illustId + ")详情");
            return ResponseEntity.ok().body(new Result<>("获取画作详情成功", adminService.queryIllustrationById(illustId)));
        }
        return ResponseEntity.ok().body(new Result<>("获取画作详情成功", null));
    }

    @PutMapping("/illusts/{illustId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Illustration>> updateIllustrationById(@PathVariable Integer illustId, @RequestHeader(value = "Token", required = false) String token, @RequestBody IllustDTO illustDTO) throws JsonProcessingException {
        adminService.updateIllusts(illustDTO);
        return ResponseEntity.ok().body(new Result<>("获取画作详情成功", null));
    }

    @PostMapping("/collections")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<List<CollectionPO>>> queryCollection(
            @RequestBody CollectionPO collectionPO,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "30") Integer pageSize,
            @RequestParam(defaultValue = "id") String orderBy,
            @RequestParam(defaultValue = "asc") String orderByMode,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Page<CollectionPO> collectionPOPage = adminService.queryCollection(collectionPO, page, pageSize, orderBy, orderByMode);
        return ResponseEntity.ok().body(new Result<>("获取画集列表成功", collectionPOPage.getTotalPages(), collectionPOPage.getContent()));
    }

    @PutMapping("/collections/{collectionId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<CollectionPO>> updateCollection(
            @PathVariable Integer collectionId,
            @RequestBody CollectionPO collectionPO,
            @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok().body(new Result<>("更新画集成功", adminService.updateCollection(collectionPO)));
    }

    @DeleteMapping("/collections/{collectionId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> deleteCollection(
            @PathVariable Integer collectionId,
            @RequestHeader(value = "Authorization", required = false) String token) {
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
            @RequestHeader(value = "Authorization", required = false) String token) {
        Page<DiscussionPO> discussionPOPage = adminService.queryDiscussion(discussionPO, page, pageSize, orderBy, orderByMode);
        return ResponseEntity.ok().body(new Result<>("获取讨论列表成功", discussionPOPage.getTotalPages(), discussionPOPage.getContent()));
    }

    @PutMapping("/discussions/{discussionId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<DiscussionPO>> updateDiscussion(
            @PathVariable Integer discussionId,
            @RequestBody DiscussionPO discussionPO,
            @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok().body(new Result<>("更新讨论成功", adminService.updateDiscussion(discussionPO)));
    }

    @DeleteMapping("/discussions/{discussionId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> deleteDiscussion(
            @PathVariable Integer discussionId,
            @RequestHeader(value = "Authorization", required = false) String token) {
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
            @RequestHeader(value = "Authorization", required = false) String token) {
        Page<SectionPO> sectionPOPage = adminService.querySection(sectionPO, page, pageSize, orderBy, orderByMode);
        return ResponseEntity.ok().body(new Result<>("获取板块列表成功", sectionPOPage.getTotalPages(), sectionPOPage.getContent()));
    }

    @PutMapping("/sections/{sectionId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<SectionPO>> updateSection(
            @PathVariable Integer sectionId,
            @RequestBody SectionPO sectionPO,
            @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok().body(new Result<>("更新板块成功", adminService.updateSection(sectionPO)));
    }

    @DeleteMapping("/sections/{sectionId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> deleteSection(
            @PathVariable Integer sectionId,
            @RequestHeader(value = "Authorization", required = false) String token) {
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
            @RequestHeader(value = "Authorization", required = false) String token) {
        Page<UserPO> userPOPage = adminService.queryUsers(userPO, page, pageSize, orderBy, orderByMode);
        return ResponseEntity.ok().body(new Result<>("获取用户列表成功", userPOPage.getTotalPages(), userPOPage.getContent()));
    }

    @PutMapping("/users/{userId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<UserPO>> updateUser(
            @PathVariable Integer userId,
            @RequestBody UserPO userPO,
            @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok().body(new Result<>("更新用户成功", adminService.updateUser(userPO)));
    }

    @DeleteMapping("/users/{userId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> deleteUser(
            @PathVariable Integer userId,
            @RequestHeader(value = "Authorization", required = false) String token) {
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
            @RequestHeader(value = "Authorization", required = false) String token) {
        Page<CommentPO> commentPOPage = adminService.queryComment(commentPO, page, pageSize, orderBy, orderByMode);
        return ResponseEntity.ok().body(new Result<>("获取评论列表成功", commentPOPage.getTotalPages(), commentPOPage.getContent()));
    }

    @PutMapping("/comments/{commentId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<CommentPO>> updateComment(
            @PathVariable Integer commentId,
            @RequestBody CommentPO commentPO,
            @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok().body(new Result<>("更新评论成功", adminService.updateComment(commentPO)));
    }

    @DeleteMapping("/comments/{commentId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> deleteComment(
            @PathVariable Integer commentId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok().body(new Result<>("删除评论成功", adminService.deleteComment(adminService.queryCommentById(commentId))));
    }

//    @PutMapping("/illusts")
//    public ResponseEntity<Result<User>> updateIllusts(@RequestBody @Valid IllustDTO illustDTO, @RequestHeader(value = "Authorization", required = false) String token) {
//        adminService.updateIllusts(illustDTO);
//        return null;
//    }

}
