package dev.cheerfun.pixivic.biz.web.admin.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.biz.web.admin.service.AdminService;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/28 5:15 PM
 * @description CacheAdminController
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RequestMapping("/admin")
public class CacheAdminController {
    private final AdminService adminService;

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
}
