package dev.cheerfun.pixivic.biz.analysis.tag.controlller;

import dev.cheerfun.pixivic.biz.analysis.tag.service.TrendingTagsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/3/13 3:10 下午
 * @description SearchRecommendController
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SearchRecommendController {
    private final TrendingTagsService trendingTagsService;

}
