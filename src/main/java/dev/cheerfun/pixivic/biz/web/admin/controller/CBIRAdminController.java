package dev.cheerfun.pixivic.biz.web.admin.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.biz.web.cbir.service.ImageReverseSearchService;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
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
 * @date 2021/5/26 12:23 AM
 * @description CBIRAdminController
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RequestMapping("/admin/cbir")
public class CBIRAdminController {
    private final ImageReverseSearchService imageReverseSearchService;

    @GetMapping("/sync")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> sync(
            @RequestParam String date,
            @RequestHeader(value = AuthConstant.AUTHORIZATION) String token) {
        return ResponseEntity.ok().body(new Result<>("同步画作到cbir服务器成功", imageReverseSearchService.syncToCBIRServer(date)));
    }

}
