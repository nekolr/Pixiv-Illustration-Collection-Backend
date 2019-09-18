package dev.cheerfun.pixivic.crawler.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.common.model.Illustration;
import dev.cheerfun.pixivic.common.model.illust.Tag;
import dev.cheerfun.pixivic.common.util.pixiv.RequestUtil;
import dev.cheerfun.pixivic.crawler.dto.IllustrationDTO;
import dev.cheerfun.pixivic.crawler.dto.RankDTO;
import dev.cheerfun.pixivic.crawler.mapper.IllustrationMapper;
import dev.cheerfun.pixivic.crawler.model.ModeMeta;
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
public class IllustrationService {
    private final RequestUtil requestUtil;
    private final IllustrationMapper illustrationMapper;
    private final ObjectMapper objectMapper;
    private static ReentrantLock lock = new ReentrantLock();
    private static final List<ModeMeta> modes;
    private static final HashMap<String, Integer> modeIndex;
    private static final Integer taskSum;
   // private static volatile List<List<Illustration>> illustrationLists;
  //  private static volatile List<String> waitForReDownload;

    static {
        taskSum = 133;
    //    illustrationLists = new ArrayList<>(Collections.nCopies(taskSum, null));
   //     waitForReDownload = new ArrayList<>(Collections.nCopies(taskSum, null));
        modeIndex = new HashMap<>(11) {{
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
        List<List<Illustration>> illustrationLists = new ArrayList<>(Collections.nCopies(taskSum, null));
        List<String> waitForReDownload = new ArrayList<>(Collections.nCopies(taskSum, null));
        modes.stream().parallel().forEach(modeMeta -> {
            IntStream.range(0, modeMeta.getTaskSum()).forEach(i -> {
                getDayRankInfo(modeMeta, date.toString(), i, waitForReDownload)
                        .whenComplete((illustrations, throwable) -> {
                            if (illustrations == null)
                                waitForReDownload.set(i + modeIndex.get(modeMeta.getMode()), modeMeta.getMode() + "-" + i);
                            else{
                                illustrationLists.set(i + modeIndex.get(modeMeta.getMode()), illustrations);
                            }
                            cd.countDown();
                        });
            });
        });
        cd.await(200, TimeUnit.SECONDS);
        while (waitForReDownload.removeIf(Objects::isNull)) {
            dealReDownload(date.toString(), waitForReDownload, illustrationLists);
        }
        System.err.println("失败队列：");
        waitForReDownload.forEach(System.out::println);
        illustrationLists.removeIf(Objects::isNull);
        saveToDb(illustrationLists.stream().flatMap(Collection::stream).filter(Objects::nonNull).collect(Collectors.toList()));
        return illustrationLists.stream().flatMap(Collection::stream).filter(Objects::nonNull).map(Illustration::getArtistId).distinct().collect(Collectors.toList());
    }

    private CompletableFuture<List<Illustration>> getDayRankInfo(ModeMeta modeMeta, String date, Integer index, List<String> waitForReDownload) {
        return getDayRankInfo(modeMeta.getMode(), date, index)
                .thenApply(result -> {
                    if ("false".equals(result)) {
                        System.err.println("获取信息失败");
                        waitForReDownload.set(index + modeIndex.get(modeMeta.getMode()), modeMeta.getMode() + "-" + index);
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

    private CompletableFuture<String> getDayRankInfo(String mode, String date, Integer index) {
        return requestUtil.getJson("https://proxy.pixivic.com:23334/v1/illust/ranking?mode=" + mode + "&offset=" + index * 30 + "&date=" + date);
    }

    private void dealReDownload(String date, List<String> waitForReDownload, List<List<Illustration>> illustrationLists) throws InterruptedException {
        final CountDownLatch cd = new CountDownLatch(waitForReDownload.size());
        waitForReDownload.stream().parallel().forEach(i -> {
            String[] split = i.split("-");
            getDayRankInfo(split[0], date, Integer.parseInt(split[1])).whenComplete((result, throwable) -> {
                        try {
                            RankDTO rankDTO = objectMapper.readValue(result, new TypeReference<RankDTO>() {
                            });
                            List<Illustration> illustrations = rankDTO.getIllusts().stream().map(IllustrationDTO::castToIllustration).collect(Collectors.toList());
                            illustrationLists.set(Integer.parseInt(split[1]) + modeIndex.get(split[0]), illustrations);
                            waitForReDownload.set(Integer.parseInt(split[1]) + modeIndex.get(split[0]), null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        cd.countDown();
                    }
            );
        });
        cd.await(waitForReDownload.size() * 2, TimeUnit.SECONDS);
    }

    @Transactional
    public void saveToDb(List<Illustration> illustrations) {
        List<Tag> tags = illustrations.stream().parallel().map(Illustration::getTags).flatMap(Collection::stream).collect(Collectors.toList());
        illustrationMapper.insertTag(tags);
        System.out.println("标签入库完毕");
        //获取标签id
        tags.forEach(tag -> tag.setId(illustrationMapper.getTagId(tag.getName(),tag.getTranslatedName())));
       // illustrations.stream().parallel().forEach(illustration -> illustration.getTags().forEach(tag -> tag.setId(illustrationMapper.getTagId(tag.getName(),tag.getTranslatedName()))));
        System.out.println("标签id取回完毕");
        illustrationMapper.insert(illustrations);
        System.out.println("画作入库完毕");
        illustrationMapper.insertTagIllustRelation(illustrations);
        System.out.println("标签与画作的联系入库完毕");
    }
}
