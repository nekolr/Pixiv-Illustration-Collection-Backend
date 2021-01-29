package dev.cheerfun.pixivic.biz.web.comment.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.basic.event.constant.ActionType;
import dev.cheerfun.pixivic.basic.event.constant.ObjectType;
import dev.cheerfun.pixivic.basic.event.domain.Event;
import dev.cheerfun.pixivic.basic.event.publisher.EventPublisher;
import dev.cheerfun.pixivic.basic.ratelimit.annotation.RateLimit;
import dev.cheerfun.pixivic.basic.sensitive.annotation.SensitiveCheck;
import dev.cheerfun.pixivic.biz.web.comment.dto.Like;
import dev.cheerfun.pixivic.biz.web.comment.po.Comment;
import dev.cheerfun.pixivic.biz.web.comment.service.CommentService;
import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.biz.web.user.service.CommonService;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/12/09 20:57
 * @description CommentController
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommentController {
    private final CommentService commentService;
    private final CommonService commonService;
    private final EventPublisher eventPublisher;

    @PostMapping("/{commentAppType}/{commentAppId}/comments")
    @PermissionRequired
    @RateLimit
    public ResponseEntity<Result<Integer>> pushComment(@PathVariable String commentAppType, @PathVariable int commentAppId, @RequestBody @SensitiveCheck Comment comment, @RequestHeader(AuthConstant.AUTHORIZATION) String token) {
        int userId = (int) AppContext.get().get(AuthConstant.USER_ID);
        if (commonService.queryUser(userId).getPhone() == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "请在绑定手机号后评论");
        }
        //TODO 接入百度云审核 修改成token校验
        comment.init(commentAppType, commentAppId, userId);
        commentService.pushComment(comment);
        eventPublisher.publish(new Event(userId, ActionType.PUBLISH, ObjectType.COMMENT, comment.getId(), LocalDateTime.now()));
        return ResponseEntity.ok().body(new Result<>("评论成功", comment.getId()));
    }

    @GetMapping("/{commentAppType}/{commentAppId}/comments")
    @PermissionRequired(PermissionLevel.ANONYMOUS)
    public ResponseEntity<Result<List<Comment>>> pullComment(@PathVariable String commentAppType, @PathVariable int commentAppId, @RequestHeader(value = AuthConstant.AUTHORIZATION, required = false) String token, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok().body(new Result<>("拉取评论成功", 0, null));
    }

    @GetMapping("/{commentAppType}/{commentAppId}/topCommentCount")
    public ResponseEntity<Result<Integer>> pullCommentCount(@PathVariable String commentAppType, @PathVariable int commentAppId, @RequestHeader(value = AuthConstant.AUTHORIZATION, required = false) String token, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok().body(new Result<>("获取顶级评论数量成功", commentService.queryTopCommentCount(commentAppType, commentAppId)));
    }

    @PostMapping("user/likedComments")
    @PermissionRequired
    public ResponseEntity<Result<String>> like(@RequestBody Like like, @RequestHeader(AuthConstant.AUTHORIZATION) String token) {
        int userId = (int) AppContext.get().get(AuthConstant.USER_ID);
        commentService.likeComment(like, userId);
        //产生通知事件
        eventPublisher.publish(new Event(userId, ActionType.LIKE, ObjectType.COMMENT, like.getCommentId(), LocalDateTime.now()));
        return ResponseEntity.ok().body(new Result<>("点赞成功"));
    }

    @DeleteMapping("user/likedComments/{commentAppType}/{commentAppId}/{commentId}")
    @PermissionRequired
    public ResponseEntity<Result<String>> cancelLike(@PathVariable String commentAppType, @PathVariable Integer commentAppId, @PathVariable Integer commentId, @RequestHeader(AuthConstant.AUTHORIZATION) String token) {
        commentService.cancelLikeComment((int) AppContext.get().get(AuthConstant.USER_ID), new Like(commentAppType, commentAppId, commentId));
        return ResponseEntity.ok().body(new Result<>("取消点赞成功"));
    }

    @GetMapping("/comments/{commentId}")
    @PermissionRequired(PermissionLevel.ANONYMOUS)
    public ResponseEntity<Result<Comment>> pullCommentById(@PathVariable Integer commentId, @RequestHeader(value = AuthConstant.AUTHORIZATION, required = false) String token) {
        return ResponseEntity.ok().body(new Result<>("拉取单条评论成功", commentService.pullCommentById(commentId)));
    }

}
