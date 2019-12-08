package dev.cheerfun.pixivic.biz.web.user.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.biz.web.user.po.Announcement;
import dev.cheerfun.pixivic.biz.web.user.service.AnnouncementService;
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
 * @date 2019/09/14 13:01
 * @description AnnouncementController
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/announcement")
public class AnnouncementController {
    private final AnnouncementService announcementService;
    private static final String USER_ID = "userId";

    @PostMapping
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<String>> send(@RequestHeader("Authorization") String token, @RequestBody Announcement announcement) {
        announcementService.send(announcement);
        return ResponseEntity.ok().body(new Result<>("公告发布成功"));
    }

    @GetMapping
    @PermissionRequired
    public ResponseEntity<Result<List<Announcement>>> query(@RequestHeader("Authorization") String token, @RequestParam String date) {
        return ResponseEntity.ok().body(new Result<>("获取系统公告成功", announcementService.query((int) AppContext.get().get(USER_ID), date)));
    }

}
