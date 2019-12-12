package dev.cheerfun.pixivic.biz.crawler.pixiv.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
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
public class MainCrawlerService {
    private final IllustrationService illustrationService;
    private final ArtistService artistService;

    @CacheEvict(cacheNames = "illust", allEntries = true)
    public void dailyPersistentTask() throws InterruptedException {
        int hour = LocalTime.now().getHour();
        LocalDate date = LocalDate.now().plusDays(-(hour + 1));
        System.out.println(date);
        List<Integer> artistIds = illustrationService.pullAllRankInfo(date);
        artistService.pullArtistsInfo(artistIds);
    }

}
