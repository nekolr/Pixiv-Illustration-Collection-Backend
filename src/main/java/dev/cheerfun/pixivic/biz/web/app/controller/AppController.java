package dev.cheerfun.pixivic.biz.web.app.controller;

import dev.cheerfun.pixivic.biz.web.app.po.AppVersionInfo;
import dev.cheerfun.pixivic.biz.web.app.po.ExternalLink;
import dev.cheerfun.pixivic.biz.web.app.service.AppService;
import dev.cheerfun.pixivic.biz.web.app.service.ExternalLinkService;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private final ExternalLinkService externalLinkService;

    @GetMapping("/latest")
    public ResponseEntity<Result<AppVersionInfo>> queryLatest(@RequestParam String version) {
        AppVersionInfo appVersionInfo = appService.queryLatestByVersion(version);
        return ResponseEntity.ok().body(new Result<>(appVersionInfo == null ? "已经是最新版" : "查询是否有最新版本成功", appVersionInfo));
    }

    @GetMapping("/versions")
    public ResponseEntity<Result<List<AppVersionInfo>>> queryVersionList(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok().body(new Result<>("查询app版本列表成功", appService.queryCount(), appService.queryList(page, pageSize)));
    }

    @GetMapping("/externalLinks")
    public ResponseEntity<Result<List<ExternalLink>>> queryExternalLinks() {
        return ResponseEntity.ok().body(new Result<>("查询外链列表成功", externalLinkService.queryAllExternalLink()));
    }

    @PostMapping("/externalLinks")
    public ResponseEntity<Result<ExternalLink>> addExternalLinks(@RequestBody ExternalLink externalLink) {
        return ResponseEntity.ok().body(new Result<>("添加外链成功", externalLinkService.add(externalLink)));
    }

    @PutMapping("/externalLinks/{externalLinkId}")
    public ResponseEntity<Result<ExternalLink>> updateExternalLinks(@RequestBody ExternalLink externalLink) {
        return ResponseEntity.ok().body(new Result<>("更新外链成功", externalLinkService.update(externalLink)));
    }

    @DeleteMapping("/externalLinks/{externalLinkId}")
    public ResponseEntity<Result<Boolean>> deleteExternalLinks(@PathVariable Integer externalLinkId) {
        return ResponseEntity.ok().body(new Result<>("删除外链成功", externalLinkService.delete(externalLinkId)));
    }

}
