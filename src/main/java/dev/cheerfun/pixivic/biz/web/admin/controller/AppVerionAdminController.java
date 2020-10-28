package dev.cheerfun.pixivic.biz.web.admin.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.biz.web.admin.po.AppVersionInfoPO;
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
 * @date 2020/10/28 5:16 PM
 * @description AppVerionAdminController
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RequestMapping("/admin")
public class AppVerionAdminController {
    private final AdminService adminService;

    //app版本管理
    @PostMapping("/appVersionInfoList")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<List<AppVersionInfoPO>>> queryAppVersionInfo(
            @RequestBody AppVersionInfoPO advertisementPO,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "30") Integer pageSize,
            @RequestParam(defaultValue = "id") String orderBy,
            @RequestParam(defaultValue = "asc") String orderByMode,
            @RequestHeader(value = "Authorization") String token) {
        Page<AppVersionInfoPO> appVersionInfoPOS = adminService.queryAppVersionInfo(advertisementPO, page, pageSize, orderBy, orderByMode);
        return ResponseEntity.ok().body(new Result<>("查询app版本记录成功", appVersionInfoPOS.getTotalElements(), appVersionInfoPOS.getContent()));
    }

    @PutMapping("/appVersionInfos/{appVersionInfoId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<AppVersionInfoPO>> updateAppVersionInfo(
            @PathVariable Integer appVersionInfoId,
            @RequestBody AppVersionInfoPO appVersionInfoPO,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("更新app版本成功", adminService.updateAppVersionInfo(appVersionInfoPO)));
    }

    @PostMapping("/appVersionInfos")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<AppVersionInfoPO>> createAppVersionInfo(
            @RequestBody AppVersionInfoPO appVersionInfoPO,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("发布app版本成功", adminService.createAppVersionInfo(appVersionInfoPO)));
    }

    @DeleteMapping("/appVersionInfos/{appVersionInfoId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> deleteAppVersionInfo(
            @PathVariable Integer appVersionInfoId,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("删除app版本成功", adminService.deleteAppVersionInfo(appVersionInfoId)));
    }

}
