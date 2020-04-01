package dev.cheerfun.pixivic.biz.web.history.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.biz.userInfo.annotation.WithUserInfo;
import dev.cheerfun.pixivic.biz.web.history.domain.IllustHistory;
import dev.cheerfun.pixivic.biz.web.history.service.IllustHistoryService;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/12 5:32 下午
 * @description IllustLogController
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IllustHistoryController {
    private final IllustHistoryService illustHistoryService;

    @PostMapping("/users/{userId}/illustHistory")
    public ResponseEntity<Result<String>> log(@RequestBody IllustHistory illustHistory) {
        illustHistoryService.push(illustHistory);
        return ResponseEntity.ok(new Result<>("记录历史记录成功"));
    }

    @PermissionRequired
    @WithUserInfo
    @GetMapping("/users/{userId}/illustHistory")
    public ResponseEntity<Result<List<Illustration>>> queryIllustHistory(@PathVariable Integer userId, @RequestParam(defaultValue = "1") @Max(30) int page, @RequestParam(defaultValue = "30") int pageSize, @RequestHeader(value = "Authorization", required = false) String token) {
        List<Illustration> illustrations = illustHistoryService.pullFromRedis((int) AppContext.get().get(AuthConstant.USER_ID), page, pageSize);
        return ResponseEntity.ok(new Result<>("获取近期历史记录成功", illustrations));
    }

    // TODO
    @PermissionRequired
    @DeleteMapping("/users/{userId}/illustHistory")
    public ResponseEntity<Result<String>> deleteIllustHistory(@PathVariable Integer userId, @RequestParam(defaultValue = "1") @Max(30) int page, @RequestParam(defaultValue = "30") int pageSize, @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok(new Result<>("删除近期历史记录成功"));
    }

    @PermissionRequired
    @WithUserInfo
    @GetMapping("/users/{userId}/oldIllustHistory")
    public ResponseEntity<Result<List<Illustration>>> queryOldIllustHistory(@PathVariable Integer userId, @RequestParam(defaultValue = "1") @Max(30) int page, @RequestParam(defaultValue = "30") int pageSize, @RequestHeader(value = "Authorization", required = false) String token) {
        List<Illustration> illustrations = illustHistoryService.pullFromMysql((int) AppContext.get().get(AuthConstant.USER_ID), page, pageSize);
        return ResponseEntity.ok(new Result<>("获取历史记录成功", illustrations));
    }

    // TODO
    @PermissionRequired
    @DeleteMapping("/users/{userId}/oldIllustHistory")
    public ResponseEntity<Result<String>> deleteOldIllustHistory(@PathVariable Integer userId, @RequestParam(defaultValue = "1") @Max(30) int page, @RequestParam(defaultValue = "30") int pageSize, @RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok(new Result<>("删除历史记录成功"));
    }
}
