package dev.cheerfun.pixivic.basic.review.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.basic.review.secmapper.IllustReviewMapper;
import dev.cheerfun.pixivic.basic.review.util.ReviewFilter;
import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/1/20 1:53 PM
 * @description IllustReviewService
 */
//@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IllustReviewService {
    private final ReviewFilter reviewFilter;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final IllustReviewMapper illustReviewMapper;

    //@PostConstruct
    public void init() {
        log.info("开始扫描画作");
        // 遍历画作数据库 每次取10000个画作 offset取之前的最后一个画作的id
        String illustReview = stringRedisTemplate.opsForValue().get("illustReview");
        Integer illustId = null;
        if (illustReview != null) {
            illustId = Integer.valueOf(illustReview);
        } else {
            illustId = 0;
        }
        boolean flag = true;

        while (flag) {
            //log.info(String.valueOf(illustId));
            stringRedisTemplate.opsForValue().set("illustReview", String.valueOf(illustId));
            List<Illustration> illustrations = illustReviewMapper.queryIllustrationByIllustId(illustId);
            if (illustrations.size() == 0) {
                return;
            }
            List<Illustration> collect = illustrations.stream().parallel().filter(e -> {
                Illustration illustration = objectMapper.convertValue(e, new TypeReference<Illustration>() {
                });
                if (illustration.getTags() != null && illustration.getTags().size() > 0) {
                    return illustration.getTags().stream().anyMatch(t -> reviewFilter.filter(t.getName()) || reviewFilter.filter(t.getTranslatedName()) || reviewFilter.filter(e.getTitle()) || reviewFilter.filter(e.getCaption()));
                } else {
                    return false;
                }

            }).collect(Collectors.toList());
            if (collect.size() > 0) {
                collect
                        .forEach(
                                e -> {
                                    if(e.getSanityLevel()<=6)
                                    illustReviewMapper.insertIntoReviewBlockTable(e.getId());
                                }
                        );
            }

            illustId = illustrations.get(illustrations.size() - 1).getId();
            //遍历画作 进行审查
            //审查不通过输入block表

        }

    }

}
