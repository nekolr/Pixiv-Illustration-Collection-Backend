package dev.cheerfun.pixivic.biz.crawler.pixiv.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.crawler.pixiv.dto.IllustrationDTO;
import dev.cheerfun.pixivic.biz.crawler.pixiv.dto.IllustsDTO;
import dev.cheerfun.pixivic.biz.crawler.pixiv.mapper.IllustrationMapper;
import dev.cheerfun.pixivic.biz.web.rank.po.Rank;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.util.pixiv.RequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
public class IllustRankService {
    private final ObjectMapper objectMapper;
    private final IllustrationMapper illustrationMapper;
    private final IllustrationService illustrationService;
    private final RequestUtil requestUtil;
    private final static String[] MODES = {"day", "week", "month", "day_female", "day_male","day_manga","week_manga","month_manga","week_rookie_manga"};

    public void pullAllRank() throws InterruptedException {
        LocalDate date = LocalDate.now().plusDays(-2);
        for (String mode : MODES) {
            illustrationMapper.insertRank(getIllustrations(mode, date.toString()));
        }
    }

    private Rank getIllustrations(String mode, String date) throws InterruptedException {
        ArrayList<Illustration> illustrations = new ArrayList<>(300);
        final CountDownLatch cd = new CountDownLatch(10);
        IntStream.range(0, 10).parallel().forEach(i -> getIllustrationsJson(mode, date, i).thenAccept(illustration -> {
            illustrations.addAll(illustration);
            cd.countDown();
        }));
        cd.await();
        illustrations.trimToSize();
        String rankMode;
        switch (mode) {
            case "day_female":
                rankMode = "female";
                break;
            case "day_male":
                rankMode = "male";
                break;
            default:
                rankMode=mode;
                break;
        }
        return new Rank(illustrations, rankMode, date);
    }

    private CompletableFuture<List<Illustration>> getIllustrationsJson(String mode, String date, Integer index) {
        return requestUtil.getJson("https://proxy.pixivic.com:23334/v1/illust/ranking?mode=" + mode + "&offset=" + index * 30 + "&date=" + date)
                .thenApply(result -> {
                    if ("false".equals(result)) {
                        System.err.println("获取信息失败");
                        return null;
                    }
                    try {
                        IllustsDTO rankDTO = objectMapper.readValue(result, new TypeReference<IllustsDTO>() {
                        });
                        return rankDTO.getIllusts().stream().filter(Objects::nonNull).map(IllustrationDTO::castToIllustration).collect(Collectors.toList());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                });
    }

    public void deal() throws IOException, InterruptedException {
        //读取文件中所有未获取的画作id
        String s = Files.readString(Paths.get("/home/oysterqaq/文档/illustIdList.txt"));
        String[] split = s.split("\n");
        //遍历
        int length = split.length;
        for (int i = 1360; i < length; i++) {
            System.out.println(split[i]);
            Illustration illustration = illustrationService.pullIllustrationInfo(Integer.parseInt(split[i]));
            if (illustration != null) {
                System.out.println(split[i] + "存在");
                List<Illustration> illustrations = new ArrayList<>();
                illustrations.add(illustration);
                illustrationMapper.insert(illustrations);
            }
            Thread.sleep(1000);
        }

        //下载
        //存入数据库
    }

}
