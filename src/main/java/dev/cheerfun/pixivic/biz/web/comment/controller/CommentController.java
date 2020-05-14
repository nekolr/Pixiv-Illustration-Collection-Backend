package dev.cheerfun.pixivic.biz.web.comment.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.basic.sensitive.annotation.SensitiveCheck;
import dev.cheerfun.pixivic.biz.event.constant.ObjectType;
import dev.cheerfun.pixivic.biz.event.domain.Event;
import dev.cheerfun.pixivic.biz.event.publisher.EventPublisher;
import dev.cheerfun.pixivic.biz.notify.constant.ActionType;
import dev.cheerfun.pixivic.biz.web.comment.dto.Like;
import dev.cheerfun.pixivic.biz.web.comment.po.Comment;
import dev.cheerfun.pixivic.biz.web.comment.service.CommentService;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final EventPublisher eventPublisher;

    @PostMapping("/{commentAppType}/{commentAppId}/comments")
    @PermissionRequired
    public ResponseEntity<Result<String>> pushComment(@PathVariable String commentAppType, @PathVariable int commentAppId, @RequestBody @SensitiveCheck Comment comment, @RequestHeader("Authorization") String token) {
        int userId = (int) AppContext.get().get(AuthConstant.USER_ID);
        comment.init(commentAppType, commentAppId, userId);
        commentService.pushComment(comment);
//        //如果不是顶层(即存在被回复人)产生通知事件
        if (comment.getReplyTo() != 0) {
            eventPublisher.publish(new Event(userId, comment.getReplyFromName(), ActionType.REPLIED, ObjectType.COMMENT, comment.getParentId(), LocalDateTime.now()));
        }
        return ResponseEntity.ok().body(new Result<>("评论成功"));
    }

    @GetMapping("/{commentAppType}/{commentAppId}/comments")
    @PermissionRequired(PermissionLevel.ANONYMOUS)
    public ResponseEntity<Result<List<Comment>>> pullComment(@PathVariable String commentAppType, @PathVariable int commentAppId, @RequestHeader(value = "Authorization", required = false) String token, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok().body(new Result<>("拉取评论成功", commentService.pullComment(commentAppType, commentAppId, page, pageSize)));
    }

    @PostMapping("user/likedComments")
    @PermissionRequired
    public ResponseEntity<Result<String>> like(@RequestBody Like like, @RequestHeader("Authorization") String token) {
        int userId = (int) AppContext.get().get(AuthConstant.USER_ID);
        commentService.likeComment(like, userId);
        //产生通知事件
        //notifyEventService.pushNotifyEvent(new NotifyEvent(userId, NotifyActionType.REPLIED, like.getCommentId(), NotifyObjectType.COMMENT, LocalDateTime.now()));
        return ResponseEntity.ok().body(new Result<>("点赞成功"));
    }

    @DeleteMapping("user/likedComments/{commentAppType}/{commentAppId}/{commentId}")
    @PermissionRequired
    public ResponseEntity<Result<String>> cancelLike(@PathVariable String commentAppType, @PathVariable Integer commentAppId, @PathVariable Integer commentId, @RequestHeader("Authorization") String token) {
        commentService.cancelLikeComment((int) AppContext.get().get(AuthConstant.USER_ID), new Like(commentAppType, commentAppId, commentId));
        return ResponseEntity.ok().body(new Result<>("取消点赞成功"));
    }
}
