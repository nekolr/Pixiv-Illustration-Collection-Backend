package dev.cheerfun.pixivic.crawler.service;

import dev.cheerfun.pixivic.common.model.Illustration;
import dev.cheerfun.pixivic.common.model.Spotlight;
import dev.cheerfun.pixivic.common.util.pixiv.RequestUtil;
import dev.cheerfun.pixivic.crawler.dto.SpotlightDTO;
import dev.cheerfun.pixivic.crawler.mapper.SpotlightMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
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
    private Pattern illustIdPattern = Pattern.compile("(?<=(?:id=))\\d+?(?=\" class=\"author)");
    private Pattern imageUrlPattern = Pattern.compile("(?<=(?:src\" content=\")).+?(?=\"><meta property=\"og:title\")");

    public void pullAllSpotlight() {
        int index = 2;
        List<List<Spotlight>> spotlightsList = new ArrayList<>();
        while (index < 3) {
            spotlightsList.add(getSpotlightInfo(index));
            index++;
        }
        List<Spotlight> spotlights = spotlightsList.stream().flatMap(Collection::stream).collect(Collectors.toList());
        dealRelationWithIllustration(spotlights);
    }

    private List<Spotlight> getSpotlightInfo(int index) {
        SpotlightDTO spotlightDTO = (SpotlightDTO) requestUtil.getJsonSync("https://app-api.pixiv.net/v1/spotlight/articles?category=all&offset=" + "&offset=" + index * 10, SpotlightDTO.class);
        assert spotlightDTO != null;
        return spotlightDTO.getSpotlightAticles();
    }

    private void dealRelationWithIllustration(List<Spotlight> spotlights) {
        spotlights.forEach(s -> {
            HttpRequest.Builder uri = HttpRequest.newBuilder()
                    .uri(URI.create(s.getArticleUrl()));
            RequestUtil.decorateHeader(uri);
            HttpRequest getArticle = uri.GET().build();
            try {
                String body = httpClient.send(getArticle, HttpResponse.BodyHandlers.ofString()).body();
                s.setThumbnail(imageUrlPattern.matcher(body).results().findFirst().get().group());
                List<Integer> illustIds = illustIdPattern.matcher(body).results().map(m -> Integer.parseInt(m.group())).collect(Collectors.toList());
                //联系入库
                spotlightMapper.insertRelation(s.getId(), illustIds);
                //查找出没在数据库的画作
                illustIds = illustrationService.queryIllustsNotInDb(illustIds);
                //拉取
                List<Illustration> illustrations = illustIds.stream().map(illustrationService::pullIllustrationInfo).filter(Objects::nonNull).collect(Collectors.toList());
                //持久化
                if(illustrations.size()!=0){
                    illustrationService.saveToDb(illustrations);
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        saveToDb(spotlights);
    }

    private void saveToDb(List<Spotlight> spotlights) {
        spotlightMapper.insert(spotlights);
    }

}
