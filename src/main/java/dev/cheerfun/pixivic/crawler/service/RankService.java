package dev.cheerfun.pixivic.crawler.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.common.model.Illustration;
import dev.cheerfun.pixivic.common.model.illust.Tag;
import dev.cheerfun.pixivic.crawler.dto.IllustrationDTO;
import dev.cheerfun.pixivic.crawler.dto.RankDTO;
import dev.cheerfun.pixivic.crawler.mapper.RankMapper;
import dev.cheerfun.pixivic.crawler.model.ModeMeta;
import dev.cheerfun.pixivic.crawler.util.HttpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/10 21:22
 * @description RankService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RankService {
    private final HttpUtil httpUtil;
    private final RankMapper rankMapper;
    private final ObjectMapper objectMapper;
    private static ReentrantLock lock = new ReentrantLock();
    private static final ArrayList<ModeMeta> modes;
    private static final HashMap<String, Integer> modeIndex;
    private static final Integer taskSum;

    static {
        taskSum = 133;
        modeIndex = new HashMap<>() {{
            put("day", 0);
            put("week", 17);
            put("month", 34);
            put("day_female", 51);
            put("day_male", 68);
            put("week_original", 85);
            put("week_rookie", 95);
            put("day_female_r18", 105);
            put("day_male_r18", 115);
            put("day_r18", 125);
            put("week_r18", 129);
        }};
        modes = new ArrayList<>(11) {{
            add(new ModeMeta("day", 17));
            add(new ModeMeta("week", 17));
            add(new ModeMeta("month", 17));
            add(new ModeMeta("day_female", 17));
            add(new ModeMeta("day_male", 17));
            add(new ModeMeta("week_original", 10));
            add(new ModeMeta("week_rookie", 10));
            add(new ModeMeta("day_female_r18", 10));
            add(new ModeMeta("day_male_r18", 10));
            add(new ModeMeta("day_r18", 4));
            add(new ModeMeta("week_r18", 4));
        }};
    }

    public List<Integer> pullAllRankInfo(LocalDate date) throws InterruptedException {
        final CountDownLatch cd = new CountDownLatch(taskSum);
        List<List<Illustration>> illustrationLists = new ArrayList<>();
        List<String> waitForReDownload = new ArrayList<>(taskSum);
        modes.stream().parallel().forEach(modeMeta -> {
            IntStream.range(0, modeMeta.getTaskSum()).forEach(i -> {
                getDayRankInfo(modeMeta, date.toString(), i, illustrationLists, waitForReDownload, cd);
            });
        });
        cd.await(10 * taskSum, TimeUnit.SECONDS);
        //重试
        waitForReDownload.removeIf(Objects::isNull);
        dealReDownload(date.toString(),waitForReDownload,illustrationLists);
        List<Integer> artistIds = illustrationLists.stream().flatMap(Collection::stream).map(Illustration::getArtistId).distinct().collect(Collectors.toList());
        return artistIds;
    }

    private CompletableFuture<String> getDayRankInfo(ModeMeta modeMeta, String date, Integer index, List<List<Illustration>> illustrationLists, List<String> waitForReDownload, CountDownLatch cd) {
        return getDayRankInfo(modeMeta.getMode(), date, index)
                .orTimeout(10, TimeUnit.SECONDS)
                .whenComplete((result, throwable) -> {
                    if ("false".equals(result)) {
                        waitForReDownload.add(index + modeIndex.get(modeMeta.getMode()), modeMeta.getMode() + "-" + index);
                    }
                    try {
                        RankDTO rankDTO = objectMapper.readValue(result, new TypeReference<RankDTO>() {
                        });
                        List<Illustration> illustrations = rankDTO.getIllusts().stream().map(IllustrationDTO::castToIllustration).collect(Collectors.toList());
                        illustrationLists.add(index + modeIndex.get(modeMeta.getMode()), illustrations);
                        saveToDb(illustrations);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    cd.countDown();
                });
    }

    private CompletableFuture<String> getDayRankInfo(String mode, String date, Integer index) {
        return httpUtil.getJson("https://search.api.pixivic.com/v1/illust/ranking?mode=" + mode + "&offset=" + index * 30 + "&date=" + date);
    }

    // @Scheduled(initialDelay = 1000 * 60 * 8, fixedRate = 1000 * 30)
    public void dealReDownload(String date, List<String> waitForReDownload, List<List<Illustration>> illustrationLists) throws InterruptedException {
        //   Object[] waitForReDownload = this.getWaitForReDownload();
        System.out.println("开始处理待重试队列");
        final CountDownLatch cd = new CountDownLatch(waitForReDownload.size());
        waitForReDownload.stream().parallel().forEach(i -> {
            String[] split = i.split("-");
            getDayRankInfo(split[0], date, Integer.valueOf(split[1])).thenAccept(result -> {
                        try {
                            RankDTO rankDTO = objectMapper.readValue(result, new TypeReference<RankDTO>() {
                            });
                            List<Illustration> illustrations = rankDTO.getIllusts().stream().map(IllustrationDTO::castToIllustration).collect(Collectors.toList());
                            illustrationLists.add(Integer.valueOf(split[1]) + modeIndex.get(split[0]), illustrations);
                            saveToDb(illustrations);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        cd.countDown();
                    }
            );
        });
        cd.await(waitForReDownload.size(), TimeUnit.SECONDS);
    }

    @Transactional
    public void saveToDb(List<Illustration> illustrations) {
        rankMapper.insert(illustrations);
        List<Tag> tags = illustrations.stream().parallel().map(Illustration::getTags).flatMap(Collection::stream).collect(Collectors.toList());
        rankMapper.insertTag(tags);
        rankMapper.insertTagIllustRelation(illustrations);
    }
}
