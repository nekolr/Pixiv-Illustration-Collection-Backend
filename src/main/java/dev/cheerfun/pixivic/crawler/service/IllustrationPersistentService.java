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
        LocalDate today = LocalDate.now().plusDays(-1);
        List<Integer> artistIds = illustrationService.pullAllRankInfo(today);
        artistService.pullArtistsInfo(artistIds);
    }
}
