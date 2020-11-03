package dev.cheerfun.pixivic.biz.crawler.pixiv.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.crawler.pixiv.dto.IllustrationDTO;
import dev.cheerfun.pixivic.biz.crawler.pixiv.dto.IllustsDTO;
import dev.cheerfun.pixivic.biz.crawler.pixiv.mapper.IllustrationMapper;
import dev.cheerfun.pixivic.biz.web.illust.po.Rank;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.util.pixiv.RequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
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
    private final static String[] MODES = {"day", "week", "month", "day_female", "day_male", "day_manga", "week_manga", "month_manga", "week_rookie_manga"};
    private final ObjectMapper objectMapper;
    private final IllustrationMapper illustrationMapper;
    private final IllustrationService illustrationService;
    private final RequestUtil requestUtil;

    @CacheEvict(value = "rank", allEntries = true)
    public void pullAllRank() {
        int hour = LocalTime.now().getHour();
        LocalDate date = LocalDate.now().plusDays(-(hour - 12));
        //LocalDate date = LocalDate.now().plusDays(-2);
        pullAllRank(date.toString());
    }

    public void pullAllRank(String date) {
        for (String mode : MODES) {
            Rank rank = getIllustrations(mode, date);
            if (rank.getData() != null && rank.getData().size() > 0) {
                illustrationMapper.insertRank(rank);
                illustrationService.saveToDb(rank.getData());
            }
        }
        System.out.println(date + "排行爬取完毕");
    }

    private Rank getIllustrations(String mode, String date) {
        ArrayList<Illustration> illustrations = new ArrayList<>(100);
        IntStream.range(0, 22).forEach(i -> {
            try {
                illustrations.addAll(getIllustrationsJson(mode, date, i));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                System.out.println("重试");
                try {
                    illustrations.addAll(getIllustrationsJson(mode, date, i));
                } catch (ExecutionException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });
        String rankMode;
        switch (mode) {
            case "day_female":
                rankMode = "female";
                break;
            case "day_male":
                rankMode = "male";
                break;
            default:
                rankMode = mode;
                break;
        }
        return new Rank(illustrations, rankMode, date);
    }

    private List<Illustration> getIllustrationsJson(String mode, String date, Integer index) throws ExecutionException, InterruptedException {
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
                }).get();
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
                illustrationMapper.batchInsert(illustrations);
            }
            Thread.sleep(1000);
        }

        //下载
        //存入数据库
    }

}
