package dev.cheerfun.pixivic.biz.crawler.pixiv.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.crawler.pixiv.service.DailyTaskService;
import dev.cheerfun.pixivic.biz.crawler.pixiv.service.IllustRankService;
import dev.cheerfun.pixivic.biz.crawler.pixiv.service.IllustrationService;
import dev.cheerfun.pixivic.biz.web.illust.po.Rank;
import dev.cheerfun.pixivic.biz.web.illust.service.RankService;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    private final IllustRankService illustRankService;
    private final RankService rankService;
    private final IllustrationService illustrationService;
    private final DailyTaskService dailyTaskService;
    private final ObjectMapper objectMapper;

    @GetMapping("/reSyncRank")
    public ResponseEntity<Result<String>> reSyncRank(@RequestParam String date) throws InterruptedException {
        illustRankService.pullAllRank(date);
        return ResponseEntity.ok().body(new Result<>("抓取日排行成功"));
    }

/*    @GetMapping("/mainCrawler")
    public ResponseEntity<Result<String>> mainCrawler(@RequestParam String date) throws InterruptedException {
        dailyTaskService.mainCrawler();
        return ResponseEntity.ok().body(new Result<>("全量抓取成功"));
    }*/

    @GetMapping("/reSyncRankToIllust")
    public ResponseEntity<Result<String>> reSyncRankToIllust(@RequestParam String date) {
        List<Rank> rankList = rankService.queryByDate(date);
        rankList.forEach(e -> illustrationService.saveToDb(objectMapper.convertValue(e.getData(), new TypeReference<List<Illustration>>() {
        })));
        return ResponseEntity.ok().body(new Result<>("抓取日排行成功"));
    }
}
