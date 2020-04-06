package dev.cheerfun.pixivic.biz.crawler.pixiv.service;

import dev.cheerfun.pixivic.biz.crawler.pixiv.dto.SpotlightDTO;
import dev.cheerfun.pixivic.biz.crawler.pixiv.mapper.SpotlightMapper;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Spotlight;
import dev.cheerfun.pixivic.common.util.pixiv.RequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/27 17:29
 * @description SpotlightService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SpotlightService {
    private final RequestUtil requestUtil;
    private final IllustrationService illustrationService;
    private final SpotlightMapper spotlightMapper;
    private final HttpClient httpClient;
    private final Pattern illustIdPattern = Pattern.compile("(?<=(?:id=\"illust-))\\d+");
    private final Pattern imageUrlPattern = Pattern.compile("(?<=(?:src\" content=\")).+?(?=\"><meta property=\"og:title\")");

    //@CacheEvict(cacheNames = "spotlight", allEntries = true)
    public void pullAllSpotlight() {
        int index = 0;
        List<List<Spotlight>> spotlightsList = new ArrayList<>();
        while (index < 10) {
            spotlightsList.add(getSpotlightInfo(index));
            index++;
        }
        List<Spotlight> spotlights = spotlightsList.stream().flatMap(Collection::stream).collect(Collectors.toList());
        dealRelationWithIllustration(spotlights);
    }

/*    public void deal(){

        dealRelationWithIllustration(spotlightMapper.queryAll());
    }*/

    private List<Spotlight> getSpotlightInfo(int index) {
        SpotlightDTO spotlightDTO = (SpotlightDTO) requestUtil.getJsonSync("https://proxy.pixivic.com:23334/v1/spotlight/articles?category=all&offset=" + index * 10, SpotlightDTO.class);
        assert spotlightDTO != null;
        return spotlightDTO.getSpotlightAticles();
    }

    private void dealRelationWithIllustration(List<Spotlight> spotlights) {
        spotlights.stream().parallel().forEach(s -> {
            HttpRequest.Builder uri = HttpRequest.newBuilder()
                    .uri(URI.create(s.getArticleUrl()));
            RequestUtil.decorateHeader(uri);
            HttpRequest getArticle = uri.GET().build();
            try {
                String body = httpClient.send(getArticle, HttpResponse.BodyHandlers.ofString()).body();
                Optional<MatchResult> first = imageUrlPattern.matcher(body).results().findFirst();
                first.ifPresent(matchResult -> s.setThumbnail(matchResult.group()));
                List<Integer> illustIds = illustIdPattern.matcher(body).results().map(m -> Integer.parseInt(m.group())).collect(Collectors.toList());
                //联系入库
                if (illustIds.size() > 0) {
                    spotlightMapper.insertRelation(s.getId(), illustIds);
                    //查找出没在数据库的画作
                    illustIds = illustrationService.queryIllustsNotInDb(illustIds);
                    //拉取
                    if (illustIds.size() > 0) {
                        List<Illustration> illustrations = illustIds.stream().map(illustrationService::pullIllustrationInfo).filter(Objects::nonNull).collect(Collectors.toList());
                        //持久化
                        if (illustrations.size() > 0) {
                            illustrationService.saveToDb(illustrations);
                        }
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        saveToDb(spotlights);
        System.out.println("spotlight处理完毕");
    }

    private void saveToDb(List<Spotlight> spotlights) {
        spotlightMapper.insert(spotlights);
    }

}
