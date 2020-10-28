package dev.cheerfun.pixivic.biz.web.admin.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.biz.web.admin.po.CommentPO;
import dev.cheerfun.pixivic.biz.web.admin.service.AdminService;
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
 * @date 2020/10/28 5:13 PM
 * @description CommentAdminController
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RequestMapping("/admin")
public class CommentAdminController {
    private final AdminService adminService;

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
}
