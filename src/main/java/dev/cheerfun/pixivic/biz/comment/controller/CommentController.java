package dev.cheerfun.pixivic.biz.comment.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.sensitive.annotation.SensitiveCheck;
import dev.cheerfun.pixivic.biz.comment.dto.Like;
import dev.cheerfun.pixivic.biz.comment.po.Comment;
import dev.cheerfun.pixivic.biz.comment.service.CommentService;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/12/09 20:57
 * @description CommentController
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@PermissionRequired
public class CommentController {
    private final CommentService commentService;
    private static final String USER_ID = "userId";

    @PostMapping("/{commentAppType}/{commentAppId}/comment")
    public ResponseEntity<Result<String>> pushComment(@PathVariable String commentAppType, @PathVariable int commentAppId, @RequestBody @SensitiveCheck Comment comment, @RequestHeader("Authorization") String token) {
        comment.init(commentAppType, commentAppId, (int) AppContext.get().get(USER_ID));
        commentService.pushComment(comment);
        return ResponseEntity.ok().body(new Result<>("评论成功"));
    }

    @GetMapping("/{commentAppType}/{commentAppId}/comment")
    public ResponseEntity<Result<List<Comment>>> pullComment(@PathVariable String commentAppType, @PathVariable int commentAppId, @RequestBody @SensitiveCheck Comment comment, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("拉取评论成功", commentService.pullComment(commentAppType, commentAppId,(int) AppContext.get().get(USER_ID))));
    }

    @PostMapping("user/likedComment")
    public ResponseEntity<Result<String>> like(@RequestBody Like like, @RequestHeader("Authorization") String token) {
        commentService.likeComment(like, (int) AppContext.get().get(USER_ID));
        return ResponseEntity.ok().body(new Result<>("点赞成功"));
    }

    @DeleteMapping("user/likedComment/{commentAppType}/{commentAppId}/{commentId}")
    public ResponseEntity<Result<String>> cancelLike(@PathVariable String commentAppType, @PathVariable Integer commentAppId, @PathVariable Integer commentId, @RequestHeader("Authorization") String token) {
        commentService.cancelLikeComment((int) AppContext.get().get(USER_ID), new Like(commentAppType, commentAppId, commentId));
        return ResponseEntity.ok().body(new Result<>("评论成功"));
    }
}
