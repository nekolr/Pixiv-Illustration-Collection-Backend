package dev.cheerfun.pixivic.biz.recommend.service;

import lombok.RequiredArgsConstructor;
import org.apache.mahout.cf.taste.common.TasteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/7/13 10:05 上午
 * @description RecommendDailyTaskService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RecommendDailyTaskService {
    private final RecommendService illustBookmarkRecommendService;
    private final RecommendService illustViewRecommendService;
    private final RecommendService artistRecommendService;

    @Scheduled(cron = "0 50 4 * * ?")
    public void genarateTask() throws TasteException {
        illustBookmarkRecommendService.recommend();
        artistRecommendService.recommend();
        illustViewRecommendService.recommend();
    }

}
