package dev.cheerfun.pixivic.biz.web.admin.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.biz.web.admin.po.DiscussionPO;
import dev.cheerfun.pixivic.biz.web.admin.po.SectionPO;
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
 * @date 2020/10/28 5:12 PM
 * @description DiscussionAdminCOntroller
 */

@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RequestMapping("/admin")
public class DiscussionAdminController {
    private final AdminService adminService;

    @PostMapping("/discussions")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<List<DiscussionPO>>> queryDiscussion(
            @RequestBody DiscussionPO discussionPO,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "30") Integer pageSize,
            @RequestParam(defaultValue = "id") String orderBy,
            @RequestParam(defaultValue = "asc") String orderByMode,
            @RequestHeader(value = "Authorization") String token) {
        Page<DiscussionPO> discussionPOPage = adminService.queryDiscussion(discussionPO, page, pageSize, orderBy, orderByMode);
        return ResponseEntity.ok().body(new Result<>("获取讨论列表成功", discussionPOPage.getTotalElements(), discussionPOPage.getContent()));
    }

    @PutMapping("/discussions/{discussionId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<DiscussionPO>> updateDiscussion(
            @PathVariable Integer discussionId,
            @RequestBody DiscussionPO discussionPO,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("更新讨论成功", adminService.updateDiscussion(discussionPO)));
    }

    @DeleteMapping("/discussions/{discussionId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> deleteDiscussion(
            @PathVariable Integer discussionId,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("删除讨论成功", adminService.deleteDiscussion(discussionId)));
    }

    @PostMapping("/sections")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<List<SectionPO>>> querySection(
            @RequestBody SectionPO sectionPO,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "30") Integer pageSize,
            @RequestParam(defaultValue = "id") String orderBy,
            @RequestParam(defaultValue = "asc") String orderByMode,
            @RequestHeader(value = "Authorization") String token) {
        Page<SectionPO> sectionPOPage = adminService.querySection(sectionPO, page, pageSize, orderBy, orderByMode);
        return ResponseEntity.ok().body(new Result<>("获取板块列表成功", sectionPOPage.getTotalElements(), sectionPOPage.getContent()));
    }

    @PutMapping("/sections/{sectionId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<SectionPO>> updateSection(
            @PathVariable Integer sectionId,
            @RequestBody SectionPO sectionPO,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("更新板块成功", adminService.updateSection(sectionPO)));
    }

    @DeleteMapping("/sections/{sectionId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> deleteSection(
            @PathVariable Integer sectionId,
            @RequestHeader(value = "Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("删除板块成功", adminService.deleteSection(sectionId)));
    }

}
