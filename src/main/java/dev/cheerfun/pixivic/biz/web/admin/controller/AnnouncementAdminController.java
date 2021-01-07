package dev.cheerfun.pixivic.biz.web.admin.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.biz.web.admin.po.AnnouncementPO;
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
 * @date 2020/10/28 5:14 PM
 * @description AnnouncementAdminController
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RequestMapping("/admin")
public class AnnouncementAdminController {
    private final AdminService adminService;

    @PostMapping("/announcements")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<List<AnnouncementPO>>> queryAnnouncements(
            @RequestBody AnnouncementPO announcementPO,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "30") Integer pageSize,
            @RequestParam(defaultValue = "id") String orderBy,
            @RequestParam(defaultValue = "asc") String orderByMode,
            @RequestHeader(value = AuthConstant.AUTHORIZATION) String token) {
        Page<AnnouncementPO> announcementPOS = adminService.queryAnnouncement(announcementPO, page, pageSize, orderBy, orderByMode);
        return ResponseEntity.ok().body(new Result<>("获取公告列表成功", announcementPOS.getTotalElements(), announcementPOS.getContent()));
    }

    @PutMapping("/announcements/{announcementId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<AnnouncementPO>> updateAnnouncement(
            @PathVariable Integer announcementId,
            @RequestBody AnnouncementPO announcementPO,
            @RequestHeader(value = AuthConstant.AUTHORIZATION) String token) {
        return ResponseEntity.ok().body(new Result<>("更新公告成功", adminService.updateAnnouncement(announcementPO)));
    }

    @PutMapping("/announcements")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<AnnouncementPO>> createAnnouncement(
            @RequestBody AnnouncementPO announcementPO,
            @RequestHeader(value = AuthConstant.AUTHORIZATION) String token) {
        return ResponseEntity.ok().body(new Result<>("发布公告成功", adminService.createAnnouncement(announcementPO)));
    }

    @DeleteMapping("/announcements/{announcementId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> deleteAnnouncement(
            @PathVariable Integer announcementId,
            @RequestHeader(value = AuthConstant.AUTHORIZATION) String token) {
        return ResponseEntity.ok().body(new Result<>("删除公告成功", adminService.deleteAnnouncement(announcementId)));
    }
}
