package dev.cheerfun.pixivic.biz.crawler.pixiv.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.crawler.pixiv.domain.ModeMeta;
import dev.cheerfun.pixivic.biz.crawler.pixiv.dto.IllustrationDTO;
import dev.cheerfun.pixivic.biz.crawler.pixiv.dto.IllustrationDetailDTO;
import dev.cheerfun.pixivic.biz.crawler.pixiv.dto.IllustsDTO;
import dev.cheerfun.pixivic.biz.crawler.pixiv.mapper.ArtistIllustRelationMapper;
import dev.cheerfun.pixivic.biz.crawler.pixiv.secmapper.IllustrationMapper;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import dev.cheerfun.pixivic.common.util.pixiv.RequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    private static final List<ModeMeta> modes;
    private static final HashMap<String, Integer> modeIndex;
    private static final Integer taskSum;
    private static ReentrantLock lock = new ReentrantLock();

    static {
        taskSum = 162;
        modeIndex = new HashMap<>(14) {{
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
            put("day_manga", 146);
            put("week_manga", 150);
            put("month_manga", 154);
            put("week_rookie_manga", 158);
        }};
        modes = new ArrayList<>(15) {{
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
            add(new ModeMeta("day_manga", 17));
            add(new ModeMeta("week_manga", 4));
            add(new ModeMeta("month_manga", 4));
            add(new ModeMeta("week_rookie_manga", 4));
        }};
    }

    private final RequestUtil requestUtil;
    private final IllustrationMapper illustrationMapper;
    private final ObjectMapper objectMapper;
    private final ArtistIllustRelationMapper artistIllustRelationMapper;

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
                            else {
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
                        IllustsDTO rankDTO = objectMapper.readValue(result, new TypeReference<IllustsDTO>() {
                        });
                        return rankDTO.getIllusts().stream().filter(Objects::nonNull).map(IllustrationDTO::castToIllustration).collect(Collectors.toList());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                });
    }

    private CompletableFuture<String> getDayRankInfo(String mode, String date, Integer index) {
        return requestUtil.getJson("http://proxy.pixivic.com:23334/v1/illust/ranking?mode=" + mode + "&offset=" + index * 30 + "&date=" + date);
    }

    private void dealReDownload(String date, List<String> waitForReDownload, List<List<Illustration>> illustrationLists) throws InterruptedException {
        final CountDownLatch cd = new CountDownLatch(waitForReDownload.size());
        waitForReDownload.stream().parallel().forEach(i -> {
            String[] split = i.split("-");
            getDayRankInfo(split[0], date, Integer.parseInt(split[1])).whenComplete((result, throwable) -> {
                        try {
                            IllustsDTO rankDTO = objectMapper.readValue(result, new TypeReference<IllustsDTO>() {
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

    @Transactional(rollbackFor = Exception.class)
    @Async("saveToDBExecutorService")
    public void saveToDb(List<Illustration> illustrations) {
        List<Tag> tags = illustrations.stream().parallel().map(Illustration::getTags).flatMap(Collection::stream).collect(Collectors.toList());
        if (tags.size() > 0) {
            illustrationMapper.insertTag(tags);
            //Lists.partition(tags,50).forEach(illustrationMapper::insertTag);
            System.out.println("标签入库完毕");
            //获取标签id
            tags.forEach(tag -> tag.setId(illustrationMapper.getTagId(tag.getName(), tag.getTranslatedName())));
            System.out.println("标签id取回完毕");
            illustrationMapper.insertTagIllustRelation(illustrations);
            System.out.println("标签与画作的联系入库完毕");
        } else {
            System.out.println("标签为null");
        }
        //更新画师名称与账号
        illustrations.stream().map(Illustration::getArtistPreView).forEach(illustrationMapper::updateArtistPreView);
        //Lists.partition(illustrations, 50).forEach(illustrationMapper::insert);
        illustrationMapper.batchInsert(illustrations);
        System.out.println("画作入库完毕");
        insertArtistIllustRelation(illustrations);
        System.out.println("画师画作关系入库完毕");
        //illustrationMapper.flush();
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED, transactionManager = "SecondaryTransactionManager")
    public void insertArtistIllustRelation(List<Illustration> illustrations) {
        try {
            artistIllustRelationMapper.batchiInsertArtistIllustRelation(illustrations);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public void saveToDb2(List<Illustration> illustrations) {
        List<Tag> tags = illustrations.stream().parallel().map(Illustration::getTags).flatMap(Collection::stream).collect(Collectors.toList());
        if (tags.size() > 0) {
            illustrationMapper.insertTag(tags);
            //Lists.partition(tags,50).forEach(illustrationMapper::insertTag);
            System.out.println("标签入库完毕");
            //获取标签id
            tags.forEach(tag -> tag.setId(illustrationMapper.getTagId(tag.getName(), tag.getTranslatedName())));
            System.out.println("标签id取回完毕");
            illustrationMapper.insertTagIllustRelation(illustrations);
            System.out.println("标签与画作的联系入库完毕");
        } else {
            System.out.println("标签为null");
        }
        //Lists.partition(illustrations, 50).forEach(illustrationMapper::insert);
        illustrationMapper.batchInsert(illustrations);
        System.out.println("画作入库完毕");
        illustrationMapper.flush();
    }

    public Illustration pullIllustrationInfo(Integer illustId) {
/*        CompletableFuture<String> json = requestUtil.getJson("http://proxy.pixivic.com:23334/v1/illust/detail?illust_id=" + illustId);
        try {
            System.out.println(json.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }*/
        IllustrationDetailDTO illustrationDetailDTO = (IllustrationDetailDTO) requestUtil.getJsonSync("http://proxy.pixivic.com:23334/v1/illust/detail?illust_id=" + illustId, IllustrationDetailDTO.class);
        if (illustrationDetailDTO != null) {
            return IllustrationDTO.castToIllustration(illustrationDetailDTO.getIllustrationDTO());
        }
        return null;
    }

    public List<Integer> queryIllustsNotInDb(List<Integer> illustIds) {
        return illustrationMapper.queryIllustsNotInDb(illustIds);
    }
}
