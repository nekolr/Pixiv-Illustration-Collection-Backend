package dev.cheerfun.pixivic.biz.analysis.tag.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.basic.sensitive.util.SensitiveFilter;
import dev.cheerfun.pixivic.biz.analysis.tag.dto.PixivTrendingTagResponse;
import dev.cheerfun.pixivic.biz.analysis.tag.mapper.TrendingTagsMapper;
import dev.cheerfun.pixivic.biz.analysis.tag.po.TrendingTags;
import dev.cheerfun.pixivic.biz.crawler.pixiv.dto.IllustrationDTO;
import dev.cheerfun.pixivic.biz.crawler.pixiv.dto.IllustsDTO;
import dev.cheerfun.pixivic.biz.crawler.pixiv.mapper.IllustrationMapper;
import dev.cheerfun.pixivic.biz.web.search.service.SearchService;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.illust.Tag;
import dev.cheerfun.pixivic.common.util.pixiv.RequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/13 3:04 下午
 * @description SearchRecommendService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TrendingTagsService {
    @Value("${apiLog.path}")
    private String logPath;
    private final SensitiveFilter sensitiveFilter;
    private final IllustrationMapper illustrationMapper;
    private final RequestUtil requestUtil;
    private final SearchService searchService;
    private final TrendingTagsMapper trendingTagsMapper;
    private final ObjectMapper objectMapper;
    private final static String LOG_POS = ".log";

    @Cacheable("trending_tags")
    public List<TrendingTags> queryByDate(String date) throws JsonProcessingException {
        return objectMapper.readValue(trendingTagsMapper.queryByDate(date), List.class);
        // return trendingTagsMapper.queryByDate(date);
    }

    @Scheduled(cron = "0 30 0 * * ?")
    public void dailyTask() throws IOException {
        //获取pixiv原生热度标签
        List<Tag> tagList = queryPixivTrendingTag();
        Set<String> tagNameSet = tagList.stream().collect(groupingBy(Tag::getName)).keySet();
        //读取日志
        LocalDate yesterday = LocalDate.now().plusDays(-1);
        try (Stream<String> stream = Files.lines(Paths.get(logPath, yesterday + LOG_POS), StandardCharsets.ISO_8859_1)) {
            //逐行处理
            tagList.addAll(stream.map(line -> {
                //搜索api
                if (line.contains("\"request\": \"GET /illustrations?")) {
                    //提取参数并且过滤
                    String params = line.substring(line.indexOf("GET ") + 4, line.indexOf(" HTTP"));
                    MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUriString(params).build().getQueryParams();
                    String keyword = queryParams.getFirst("keyword");
                    return sensitiveFilter.filter(URLDecoder.decode(keyword.replaceAll("%(?![0-9a-fA-F]{2})", "%25")));
                }
                return null;
            }).filter(e -> e != null && !"".equals(e) && !e.contains("*") && !tagNameSet.contains(e)).collect(groupingBy(Function.identity(), counting()))
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .limit(100)
                    .map(e -> {
                        //查找标签库中有翻译的，则是优秀标签
                        List<Tag> tags = trendingTagsMapper.queryTag(e.getKey());
                        if (tags != null && tags.size() > 0) {
                            Optional<Tag> t = tags.stream().filter(tag -> !"".equals(tag.getName()) && !"".equals(tag.getTranslatedName())).findAny();
                            return t.orElseGet(() -> tags.get(0));
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .limit(20).map(e -> {
                        try {
                            Illustration illustration = searchService.queryFirstSearchResult(e.getName());
                            if (illustration != null) {
                                return new TrendingTags(e.getName(), e.getTranslatedName(), illustration);
                            }
                        } catch (ExecutionException | InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        return null;
                    }).collect(Collectors.toList()));
            //综合pixiv原生标签后打乱
            Collections.shuffle(tagList);
            //持久化
            trendingTagsMapper.insert(LocalDate.now().plusDays(1).toString(), tagList);
        }

    }

    public List<Tag> queryPixivTrendingTag() {
        PixivTrendingTagResponse pixivTrendingTagResponse = (PixivTrendingTagResponse) requestUtil.getJsonSync("https://proxy.pixivic.com:23334/v1/trending-tags/illust?filter=for_ios", PixivTrendingTagResponse.class);
        assert pixivTrendingTagResponse != null;
        List<Tag> searchRecommends = pixivTrendingTagResponse.getTrendTags().stream().map(e -> {
            Illustration illustration = IllustrationDTO.castToIllustration(e.getIllust());
            illustrationMapper.simpleInsert(illustration);
            return new TrendingTags(e.getTag(), e.getTranslatedName(), illustration);
        }).collect(Collectors.toList());
        //持久化
        illustrationMapper.insertTag(searchRecommends);
        return searchRecommends;
    }

}
