package dev.cheerfun.pixivic.biz.web.admin.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.biz.web.admin.po.UserPO;
import dev.cheerfun.pixivic.biz.web.admin.service.AdminService;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
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
 * @description UserAdminController
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RequestMapping("/admin")
public class UserAdminController {
    private final AdminService adminService;

    @PostMapping("/users")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<List<UserPO>>> queryUsers(
            @RequestBody UserPO userPO,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "30") Integer pageSize,
            @RequestParam(defaultValue = "id") String orderBy,
            @RequestParam(defaultValue = "asc") String orderByMode,
            @RequestHeader(value = AuthConstant.AUTHORIZATION) String token) {
        Page<UserPO> userPOPage = adminService.queryUsers(userPO, page, pageSize, orderBy, orderByMode);
        return ResponseEntity.ok().body(new Result<>("获取用户列表成功", userPOPage.getTotalElements(), userPOPage.getContent()));
    }

    @PostMapping("/banUsers/{userId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<UserPO>> banUser(
            @PathVariable Integer userId,
            @RequestBody UserPO userPO,
            @RequestHeader(value = AuthConstant.AUTHORIZATION) String token) {
        return ResponseEntity.ok().body(new Result<>("封禁用户成功", adminService.updateUser(userPO)));
    }

    @DeleteMapping("/banUsers/{userId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<UserPO>> releaseUser(
            @PathVariable Integer userId,
            @RequestBody UserPO userPO,
            @RequestHeader(value = AuthConstant.AUTHORIZATION) String token) {
        return ResponseEntity.ok().body(new Result<>("解封用户成功", adminService.updateUser(userPO)));
    }

    @DeleteMapping("/users/{userId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> deleteUser(
            @PathVariable Integer userId,
            @RequestHeader(value = AuthConstant.AUTHORIZATION) String token) {
        return ResponseEntity.ok().body(new Result<>("删除用户成功", adminService.deleteUser(userId)));
    }

    //推荐管理
    @DeleteMapping("/recommendation/viewIllust")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> deleteViewIllustRecommendation(
            @RequestHeader(value = AuthConstant.AUTHORIZATION) String token) {
        return ResponseEntity.ok().body(new Result<>("删除可能想看推荐成功", adminService.deleteViewIllustRecommendation()));
    }
}
