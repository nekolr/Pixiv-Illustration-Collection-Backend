package dev.cheerfun.pixivic.biz.web.admin.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.biz.web.admin.po.CollectionPO;
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
 * @date 2020/10/28 5:11 PM
 * @description CollectionAdminController
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RequestMapping("/admin")
public class CollectionAdminController {
    private final AdminService adminService;

    @PostMapping("/collections")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<List<CollectionPO>>> queryCollection(
            @RequestBody CollectionPO collectionPO,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "30") Integer pageSize,
            @RequestParam(defaultValue = "id") String orderBy,
            @RequestParam(defaultValue = "asc") String orderByMode,
            @RequestHeader(value = AuthConstant.AUTHORIZATION) String token) {
        Page<CollectionPO> collectionPOPage = adminService.queryCollection(collectionPO, page, pageSize, orderBy, orderByMode);
        return ResponseEntity.ok().body(new Result<>("获取画集列表成功", collectionPOPage.getTotalElements(), collectionPOPage.getContent()));
    }

    @PutMapping("/collections/{collectionId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<CollectionPO>> updateCollection(
            @PathVariable Integer collectionId,
            @RequestBody CollectionPO collectionPO,
            @RequestHeader(value = AuthConstant.AUTHORIZATION) String token) {
        return ResponseEntity.ok().body(new Result<>("更新画集成功", adminService.updateCollection(collectionPO)));
    }

    @DeleteMapping("/collections/{collectionId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> deleteCollection(
            @PathVariable Integer collectionId,
            @RequestHeader(value = AuthConstant.AUTHORIZATION) String token) {
        return ResponseEntity.ok().body(new Result<>("删除画集成功", adminService.deleteCollection(collectionId)));
    }
}
