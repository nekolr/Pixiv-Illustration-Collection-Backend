package dev.cheerfun.pixivic.biz.crawler.pixiv.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.crawler.pixiv.dto.ArtistDTO;
import dev.cheerfun.pixivic.biz.crawler.pixiv.dto.IllustrationDTO;
import dev.cheerfun.pixivic.biz.crawler.pixiv.dto.IllustsDTO;
import dev.cheerfun.pixivic.biz.crawler.pixiv.mapper.ArtistMapper;
import dev.cheerfun.pixivic.biz.crawler.pixiv.remote.PixivService;
import dev.cheerfun.pixivic.common.po.Artist;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.util.pixiv.RequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/08/10 21:22
 * @description ArtistService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ArtistService {
    private final RequestUtil requestUtil;
    private final ObjectMapper objectMapper;
    private final ArtistMapper artistMapper;
    private final IllustrationService illustrationService;
    private final StringRedisTemplate stringRedisTemplate;
    private final PixivService pixivService;
    private ReentrantLock lock = new ReentrantLock();
    private final Random random = new Random(21);

    private List<Integer> waitForReDownload = new ArrayList<>();

    public Artist pullArtistsInfo(Integer artistId) {
        ArrayList<Integer> artistIds = new ArrayList<>(1);
        artistIds.add(artistId);
        List<Artist> artists = pullArtistsInfo(artistIds);
        if (artists != null && artists.size() > 0)
            return artists.get(0);
        return null;
    }

    public void pullArtistIllustList() throws IOException, InterruptedException {
        //初始化,查看记录
        List<String> strings = Files.readAllLines(Paths.get("/home/PIC/artistId.txt"));
        String artist = stringRedisTemplate.opsForValue().get("artist");
        String[] split = artist.split(":");
        String artistIndex = split[0];
        int offset = 0;
        //开始抓取
        for (int i = Integer.parseInt(artistIndex); i < strings.size(); i++) {
            String s = strings.get(i);
            boolean flag = true;
            //持久化到本地
            while (flag) {
                System.out.println("开始抓取第" + i + "个画师(id:" + s + ")的第" + offset + "作品");
                IllustsDTO illustrationDetailDTO = (IllustsDTO) requestUtil.getJsonSync("https://app-api.pixiv.net/v1/user/illusts?user_id=" + s + "&offset=" + offset, IllustsDTO.class);
                Files.write(Paths.get("/home/artist/" + s + "-" + offset + ".json"), objectMapper.writeValueAsString(illustrationDetailDTO).getBytes());
                if (illustrationDetailDTO.getNextUrl() == null) {
                    flag = false;
                    offset = 0;
                    stringRedisTemplate.opsForValue().set("artist", String.valueOf(i));
                } else {
                    stringRedisTemplate.opsForValue().set("artist", String.valueOf(i));
                    offset += 30;
                }
                Thread.sleep(500);
            }

        }

    }

    public void pullArtistLatestIllust(Integer artistId, String type) throws IOException {
        IllustsDTO illustrationDetailDTOPage1 = (IllustsDTO) requestUtil.getJsonSync("https://proxy.pixivic.com:23334/v1/user/illusts?user_id=" + artistId + "&offset=0&type=" + type, IllustsDTO.class);
        // IllustsDTO illustrationDetailDTOPage2 = (IllustsDTO) requestUtil.getJsonSync("https://proxy.pixivic.com:23334/v1/user/illusts?user_id=" + artistId + "&offset=30&type=" + type, IllustsDTO.class);
        //IllustsDTO illustrationDetailDTOPage1 = pixivService.pullArtistIllust(artistId, 0, type).execute().body();
        //   System.out.println(illustrationDetailDTOPage1);
        if (illustrationDetailDTOPage1 != null && illustrationDetailDTOPage1.getIllusts() != null) {
            List<Illustration> illustrationListPage1 = illustrationDetailDTOPage1.getIllusts().stream().filter(Objects::nonNull).map(IllustrationDTO::castToIllustration).collect(Collectors.toList());
            if (illustrationListPage1.size() > 0) {
                illustrationService.saveToDb(illustrationListPage1);
            }
        }
    }

    public void dealArtistIllustList() throws IOException {
        Path configFilePath = FileSystems.getDefault()
                .getPath("/home/artist/");
        Integer offset = Integer.valueOf(Objects.requireNonNull(stringRedisTemplate.opsForValue().get("offset")));
        List<Path> fileWithName = Files.walk(configFilePath)
                .filter(Files::isRegularFile)
                .sorted().collect(Collectors.toList());
       /* for(int j=0;j<178200;j++){
            System.out.println("删除"+fileWithName.get(j));
            Files.delete(fileWithName.get(j));
        }*/
        for (int i = offset; i < fileWithName.size(); i += 300) {
            System.out.println("开始处理第" + i + "个到第" + (i + 300) + "个文件");
            List<Illustration> illustrationList = fileWithName.stream().skip(i).limit(300).map(e ->
            {
                try {
                    return objectMapper.readValue(Files.readString(e), new TypeReference<IllustsDTO>() {
                    }).getIllusts();
                } catch (IOException ex) {
                    return null;
                }
            }).filter(Objects::nonNull).flatMap(Collection::stream).filter(Objects::nonNull).map(IllustrationDTO::castToIllustration).collect(Collectors.toList());
          /*  IllustsDTO illustsDTO = objectMapper.readValue(Files.readString(fileWithName.get(i)), new TypeReference<IllustsDTO>() {
            });
            List<Illustration> illustrationList = illustsDTO.getIllusts().stream().parallel().map(IllustrationDTO::castToIllustration).collect(Collectors.toList());*/
            illustrationService.saveToDb2(illustrationList);
            stringRedisTemplate.opsForValue().set("offset", String.valueOf(i));
        }
    }

    public List<Artist> pullArtistsInfo(List<Integer> artistIds) {
        List<Integer> artistIdsToDownload = artistMapper.queryArtistsNotInDb(artistIds);
        List<Artist> artistList = artistIdsToDownload.stream().parallel().distinct().map(i -> {
            try {
                CompletableFuture<Artist> artistCompletableFuture = requestUtil.getJson("https://proxy.pixivic.com:23334/v1/user/detail?user_id=" + i + "&filter=for_ios")
                        .thenApply(result -> {
                            if ("false".equals(result)) {
                                this.addToWaitingList(i);
                                return null;
                            }
                            Artist artist = null;
                            try {
                                artist = ArtistDTO.castToArtist(objectMapper.readValue(result, new TypeReference<ArtistDTO>() {
                                }));

                            } catch (IOException e) {
                                return null;
                            }
                            return artist;
                        });
                return artistCompletableFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("抓取画师信息错误" + e.getMessage());
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        if (artistList.size() != 0) {
            CompletableFuture.supplyAsync(() -> {
                //更新画师汇总
                updateArtistSummary(artistIds);
                return artistMapper.insert(artistList);
            }).thenAccept(e -> System.out.println("画师信息入库完毕"));
        }
        return artistList;
    }

    private void updateArtistSummary(List<Integer> artistIdList) {
        artistIdList.forEach(this::updateArtistSummary);
    }

    @CacheEvict(value = "artistSummarys", key = "#artistId")
    public void updateArtistSummary(Integer artistId) {
        artistMapper.updateArtistSummary(artistId);
    }

    private void dealReDownload() throws InterruptedException {
        final CountDownLatch cd = new CountDownLatch(waitForReDownload.size());
        waitForReDownload.forEach(i -> requestUtil.getJson("https://proxy.pixivic.com:23334/v1/user/detail?user_id=" + i + "&filter=for_ios").thenAccept(s -> cd.countDown()));
        cd.await(waitForReDownload.size() * 11, TimeUnit.SECONDS);
    }

    private void addToWaitingList(int id) {
        lock.lock();
        try {
            waitForReDownload.add(id);
        } finally {
            lock.unlock();
        }
    }
}
