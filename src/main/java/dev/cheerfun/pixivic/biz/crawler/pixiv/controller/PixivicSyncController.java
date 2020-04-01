package dev.cheerfun.pixivic.biz.crawler.pixiv.controller;

import dev.cheerfun.pixivic.biz.crawler.pixiv.service.IllustRankService;
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
 * @date 2020/4/1 8:40 上午
 * @description PixivicSyncController
 */
@RestController
@RequestMapping("/pixiv")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PixivicSyncController {
    private final IllustRankService rankService;

    @GetMapping("/reSyncRank")
    public ResponseEntity<Result<String>> reSyncRank(@RequestParam String date) throws InterruptedException {
        rankService.pullAllRank(date);
        return ResponseEntity.ok().body(new Result<>("抓取日排行成功"));
    }
}
