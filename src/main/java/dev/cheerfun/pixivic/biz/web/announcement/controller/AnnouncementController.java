package dev.cheerfun.pixivic.biz.web.announcement.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.biz.web.announcement.po.Announcement;
import dev.cheerfun.pixivic.biz.web.announcement.service.AnnouncementService;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/14 13:01
 * @description AnnouncementController
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/announcements")
public class AnnouncementController {
    private final AnnouncementService announcementService;

    @PostMapping
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<String>> send(@RequestHeader("Authorization") String token, @RequestBody Announcement announcement) {
        announcementService.send(announcement);
        return ResponseEntity.ok().body(new Result<>("公告发布成功"));
    }

    @GetMapping("/today")
    public ResponseEntity<Result<List<Announcement>>> queryByDate() {
        return ResponseEntity.ok().body(new Result<>("获取当天系统公告成功", announcementService.queryByDate(LocalDate.now().toString())));
    }

    @GetMapping
    public ResponseEntity<Result<List<Announcement>>> queryList(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok().body(new Result<>("获取系统公告列表成功", announcementService.queryByDate(LocalDate.now().toString())));
    }
}
