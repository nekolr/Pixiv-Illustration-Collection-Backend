package dev.cheerfun.pixivic.biz.web.notify.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.biz.notify.po.NotifyRemind;
import dev.cheerfun.pixivic.biz.web.notify.po.NotifyRemindSummary;
import dev.cheerfun.pixivic.biz.web.notify.service.NotifyService;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
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
 * @date 2020/10/9 8:11 PM
 * @description NotifyController
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NotifyController {
    private final NotifyService notifyService;

    //根据分类获取消息
    @GetMapping("/users/{userId}/reminds")
    @PermissionRequired
    public ResponseEntity<Result<List<NotifyRemind>>> queryRemind(@PathVariable String userId, @RequestParam Integer type, @RequestParam(required = false) Long offset, @RequestParam(defaultValue = "30") int pageSize, @RequestHeader(AuthConstant.AUTHORIZATION) String token) {
        if (offset == null) {
            offset = System.currentTimeMillis() / 1000L;
        }
        return ResponseEntity.ok().body(new Result<>("获取消息列表成功", notifyService.queryRemind((int) AppContext.get().get(AuthConstant.USER_ID), type, offset, pageSize)));
    }

    //获取各消息分类未读数
    @GetMapping("/users/{userId}/remindSummary")
    @PermissionRequired
    public ResponseEntity<Result<List<NotifyRemindSummary>>> queryRemindSummary(@PathVariable String userId, @RequestHeader(AuthConstant.AUTHORIZATION) String token) {
        return ResponseEntity.ok().body(new Result<>("获取消息未读统计成功", notifyService.queryRemindSummary((int) AppContext.get().get(AuthConstant.USER_ID))));
    }

    @GetMapping("/users/{userId}/unreadRemindsCount")
    @PermissionRequired
    public ResponseEntity<Result<Integer>> queryUnreadRemindsCount(@PathVariable String userId, @RequestHeader(AuthConstant.AUTHORIZATION) String token) {
        return ResponseEntity.ok().body(new Result<>("获取总未读消息数成功", notifyService.queryUnreadRemindsCount((int) AppContext.get().get(AuthConstant.USER_ID))));
    }

}
