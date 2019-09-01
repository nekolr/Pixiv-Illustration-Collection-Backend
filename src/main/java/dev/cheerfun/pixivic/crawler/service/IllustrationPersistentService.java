package dev.cheerfun.pixivic.crawler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/10 21:07
 * @description IllustrationPersistentService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IllustrationPersistentService {
    private final IllustrationService illustrationService;
    private final ArtistService artistService;


    @Scheduled(cron = "0 0 0 */1 * ?")
    public void dailyPersistentTask() throws InterruptedException {
        LocalDate today = LocalDate.parse("2019-08-12");
        System.out.println(today);
        List<Integer> artistIds = illustrationService.pullAllRankInfo(today);
        System.out.println("画作信息入库完毕");
        artistService.pullArtistsInfo(artistIds);
        System.out.println("画师信息入库完毕");
    }
}
