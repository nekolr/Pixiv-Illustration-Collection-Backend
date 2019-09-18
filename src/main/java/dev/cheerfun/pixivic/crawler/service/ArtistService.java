package dev.cheerfun.pixivic.crawler.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.common.model.Artist;
import dev.cheerfun.pixivic.common.util.pixiv.RequestUtil;
import dev.cheerfun.pixivic.crawler.dto.ArtistDTO;
import dev.cheerfun.pixivic.crawler.mapper.ArtistMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private ReentrantLock lock = new ReentrantLock();

    private List<Integer> waitForReDownload = new ArrayList<>();

    public void pullArtistsInfo(List<Integer> artistIds) throws InterruptedException {
        List<Integer> artistIdsToDownload = artistMapper.queryArtistsNotInDb(artistIds);
        //List<Artist> artists = new ArrayList<>(Collections.nCopies(taskSum, null));
        //  final CountDownLatch cd = new CountDownLatch(taskSum);
        List<Artist> artistList = artistIdsToDownload.stream().parallel().distinct().map(i -> {
            try {
                return requestUtil.getJson("https://proxy.pixivic.com:23334/v1/user/detail?user_id=" + i + "&filter=for_ios")
                        .thenApply(result -> {
                            if ("false".equals(result)) {
                                this.addToWaitingList(i);
                                return null;
                            }
                            Artist artist = null;
                            try {
                                artist = ArtistDTO.castToArtist(objectMapper.readValue(result, new TypeReference<ArtistDTO>() {
                                }));
                                //System.out.println(artist);

                                //  System.out.println(artists[i]);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            // cd.countDown();
                            return artist;
                        }).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        //     cd.await(taskSum, TimeUnit.SECONDS);
        //artists.removeIf(Objects::isNull);
      // artistList.forEach(System.out::println);
        if (artistList.size() != 0)
            artistMapper.insert(artistList);
        System.out.println("画师信息入库完毕");
    }

    private void dealReDownload() throws InterruptedException {
        final CountDownLatch cd = new CountDownLatch(waitForReDownload.size());
        waitForReDownload.forEach(i -> requestUtil.getJson("https://proxy.pixivic.com:23334/v1/user/detail?user_id=" + i + "&filter=for_ios").thenAccept(s -> cd.countDown()));
        cd.await(waitForReDownload.size() * 11, TimeUnit.SECONDS);
    }

    private void addToWaitingList(int id) {
        try {
            lock.lock();
            waitForReDownload.add(id);
        } finally {
            lock.unlock();
        }
    }
}
