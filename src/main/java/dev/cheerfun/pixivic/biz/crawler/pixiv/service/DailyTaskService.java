package dev.cheerfun.pixivic.biz.crawler.pixiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/12/02 18:39
 * @description DailyTaskService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DailyTaskService {
    private final SpotlightService spotlightService;
    private final IllustRankService rankService;
    private final MainCrawlerService mainCrawlerService;

    @Scheduled(cron = "0 30 3 * * ?")
    public void spotlight() {
        spotlightService.pullAllSpotlight();
    }

    @Scheduled(cron = "0 10 1 * * ?")
    public void rank() throws InterruptedException {
        rankService.pullAllRank();
    }

    @Scheduled(cron = "0 10 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17 * * ?")
    public void mainCrawler() throws InterruptedException {
        mainCrawlerService.dailyPersistentTask();
    }
}
