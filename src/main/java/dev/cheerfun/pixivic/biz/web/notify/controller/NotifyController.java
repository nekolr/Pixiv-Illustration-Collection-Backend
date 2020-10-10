package dev.cheerfun.pixivic.biz.web.notify.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.biz.notify.po.NotifyRemind;
import dev.cheerfun.pixivic.common.po.ACGNew;
import dev.cheerfun.pixivic.common.po.Result;
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
public class NotifyController {
    //根据分类获取消息
    @GetMapping("/users/{userId}/reminds")
    @PermissionRequired
    public ResponseEntity<Result<List<NotifyRemind>>> queryRemind(@PathVariable String userId, @RequestHeader("Authorization") String token, @RequestParam(defaultValue = "1") long offset, @RequestParam(defaultValue = "30") int pageSize) {

        return ResponseEntity.ok().body(new Result<>("获取消息列表成功", null));
    }

    //获取各消息分类未读数
    @GetMapping("/users/{userId}/remindSummary")
    @PermissionRequired
    public ResponseEntity<Result<List<NotifyRemind>>> queryRemindSummary(@PathVariable String userId, @RequestHeader("Authorization") String token) {

        return ResponseEntity.ok().body(new Result<>("获取消息未读统计成功", null));
    }

}
