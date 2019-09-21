package dev.cheerfun.pixivic.crawler.service;

import dev.cheerfun.pixivic.common.util.pixiv.OauthUtil;
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
    private final OauthUtil oauthUtil;
    private volatile int count = 1;

    @Scheduled(cron = "0 0 3,4,5,6 * * ?")
    public void dailyPersistentTask() throws InterruptedException {
        if (count < 5) {
            oauthUtil.refreshAccess_token();
            LocalDate today = LocalDate.now().plusDays(-count);
            List<Integer> artistIds = illustrationService.pullAllRankInfo(today);
            artistService.pullArtistsInfo(artistIds);
            count++;
        } else {
            count = 1;
        }
    }

}
