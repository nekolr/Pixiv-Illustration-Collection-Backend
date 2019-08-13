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
import lombok.Synchronized;
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
    private static volatile List<List<Illustration>> illustrationLists;
    private static volatile List<String> waitForReDownload;

    static {
        taskSum = 133;
        illustrationLists = new ArrayList<>(Collections.nCopies(taskSum, null));
        waitForReDownload = new ArrayList<>(Collections.nCopies(taskSum, null));
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
                                // saveToDb(illustrations,modeMeta.getMode()+i);
                            }

                            cd.countDown();
                        });
            });
        });
        cd.await(10 * taskSum, TimeUnit.SECONDS);
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
                .orTimeout(10, TimeUnit.SECONDS)
                .thenApply(result -> {
                    if ("false".equals(result)) {
                        System.err.println("获取信息失败");
                        waitForReDownload.set(index + modeIndex.get(modeMeta.getMode()), modeMeta.getMode() + "-" + index);
                        return null;
                    }
                    try {
                        System.out.println("开始进行JSON到实体类转换");
                        RankDTO rankDTO = objectMapper.readValue(result, new TypeReference<RankDTO>() {
                        });
                        System.out.println("开始进行实体类压平与转换");
                        List<Illustration> illustrations = rankDTO.getIllusts().stream().filter(Objects::nonNull).map(IllustrationDTO::castToIllustration).collect(Collectors.toList());
                        if (illustrations.size() > 0) {
                            System.out.println("开始入库");
                         //   saveToDb(illustrations,modeMeta.getMode()+index);
                            return illustrations;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                });
    }

    private CompletableFuture<String> getDayRankInfo(String mode, String date, Integer index) {
        return httpUtil.getJson("https://app-api.pixiv.net/v1/illust/ranking?mode=" + mode + "&offset=" + index * 30 + "&date=" + date);
    }

    public void dealReDownload(String date, List<String> waitForReDownload, List<List<Illustration>> illustrationLists) throws InterruptedException {
        final CountDownLatch cd = new CountDownLatch(waitForReDownload.size());
        waitForReDownload.stream().parallel().forEach(i -> {
            String[] split = i.split("-");
            getDayRankInfo(split[0], date, Integer.valueOf(split[1])).whenComplete((result, throwable) -> {
                        try {
                            System.out.println("-------------------------------------------");
                            RankDTO rankDTO = objectMapper.readValue(result, new TypeReference<RankDTO>() {
                            });
                            List<Illustration> illustrations = rankDTO.getIllusts().stream().map(IllustrationDTO::castToIllustration).collect(Collectors.toList());
                       //     System.out.println(waitForReDownload.get(Integer.valueOf(split[1]) + modeIndex.get(split[0])) == null);
                            illustrationLists.set(Integer.valueOf(split[1]) + modeIndex.get(split[0]), illustrations);
                            waitForReDownload.set(Integer.valueOf(split[1]) + modeIndex.get(split[0]), null);
                            //System.out.println(waitForReDownload.get(Integer.valueOf(split[1]) + modeIndex.get(split[0])) == null);
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
    @Synchronized
    public void saveToDb(List<Illustration> illustrations) {
        rankMapper.insert(illustrations);
        List<Tag> tags=new ArrayList<>();
        illustrations.forEach(illustration -> tags.addAll(illustration.getTags()));
        System.out.println(tags);
        rankMapper.insertTag(tags);
        rankMapper.insertTagIllustRelation(illustrations);
    }
}
