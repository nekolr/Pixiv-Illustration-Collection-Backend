package dev.cheerfun.pixivic.biz.web.announcement.controller;

import dev.cheerfun.pixivic.biz.web.announcement.po.Announcement;
import dev.cheerfun.pixivic.biz.web.announcement.service.AnnouncementService;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/today")
    public ResponseEntity<Result<List<Announcement>>> queryByDate() {
        return ResponseEntity.ok().body(new Result<>("获取当天系统公告成功", announcementService.queryByDate(LocalDate.now().toString())));
    }

    @GetMapping
    public ResponseEntity<Result<List<Announcement>>> queryList(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok().body(new Result<>("获取系统公告列表成功", announcementService.queryListCount(), announcementService.queryList(page, pageSize)));
    }

}
