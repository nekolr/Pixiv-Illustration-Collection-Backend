package dev.cheerfun.pixivic.biz.web.app.controller;

import dev.cheerfun.pixivic.biz.web.app.po.AppVersionInfo;
import dev.cheerfun.pixivic.biz.web.app.service.AppService;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/8/31 6:22 下午
 * @description AppController
 */
@RestController
@RequestMapping("/app")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AppController {
    private final AppService appService;

    @GetMapping("/latest")
    public ResponseEntity<Result<AppVersionInfo>> queryLatest(@RequestParam String version) {
        return ResponseEntity.ok().body(new Result<>("查询是否有最新版本成功", appService.queryLatestByVersion(version)));
    }
}
