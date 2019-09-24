package dev.cheerfun.pixivic.crawler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
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

    @Scheduled(cron = "0 10 4,5,6,7,8,9 * * ?")
    public void dailyPersistentTask() throws InterruptedException {
        int hour = LocalTime.now().getHour();
        LocalDate today = LocalDate.now().plusDays(-(hour-3));
        System.out.println(today);
        List<Integer> artistIds = illustrationService.pullAllRankInfo(today);
        artistService.pullArtistsInfo(artistIds);
    }

}
