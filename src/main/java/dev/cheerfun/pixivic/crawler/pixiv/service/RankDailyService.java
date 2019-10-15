package dev.cheerfun.pixivic.crawler.pixiv.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.common.model.Illustration;
import dev.cheerfun.pixivic.common.util.pixiv.RequestUtil;
import dev.cheerfun.pixivic.crawler.pixiv.dto.IllustrationDTO;
import dev.cheerfun.pixivic.crawler.pixiv.dto.RankDTO;
import dev.cheerfun.pixivic.crawler.pixiv.mapper.IllustrationMapper;
import dev.cheerfun.pixivic.web.rank.model.Rank;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/10/10 18:41
 * @description RankDailyService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RankDailyService {
    private final ObjectMapper objectMapper;
    private final IllustrationMapper illustrationMapper;
    private final RequestUtil requestUtil;
    private final static String[] modes = {"day", "week", "month"};

    @Scheduled(cron = "0 0 1 * * ?")
    public void pullAllRank() throws InterruptedException {
        LocalDate date = LocalDate.now().plusDays(-2);
        for (String mode : modes) {
            illustrationMapper.insertRank(getIllustrations(mode, date.toString()));
        }
    }

    public Rank getIllustrations(String mode, String date) throws InterruptedException {
        ArrayList<Illustration> illustrations = new ArrayList<>(300);
        final CountDownLatch cd = new CountDownLatch(10);
        IntStream.range(0, 10).parallel().forEach(i -> getIllustrationsJson(mode, date, i).thenAccept(illustration -> {
            illustrations.addAll(illustration);
            cd.countDown();
        }));
        cd.await();
        illustrations.trimToSize();
        return new Rank(illustrations, mode, date);
    }

    private CompletableFuture<List<Illustration>> getIllustrationsJson(String mode, String date, Integer index) {
        return requestUtil.getJson("https://proxy.pixivic.com:23334/v1/illust/ranking?mode=" + mode + "&offset=" + index * 30 + "&date=" + date)
                .thenApply(result -> {
                    if ("false".equals(result)) {
                        System.err.println("获取信息失败");
                        return null;
                    }
                    try {
                        RankDTO rankDTO = objectMapper.readValue(result, new TypeReference<RankDTO>() {
                        });
                        return rankDTO.getIllusts().stream().filter(Objects::nonNull).map(IllustrationDTO::castToIllustration).collect(Collectors.toList());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                });
    }

}
