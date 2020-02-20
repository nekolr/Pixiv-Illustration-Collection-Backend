package dev.cheerfun.pixivic.basic.ad.aop;

import dev.cheerfun.pixivic.basic.ad.domain.Advertisement;
import dev.cheerfun.pixivic.basic.ad.domain.AdvertisementInfo;
import dev.cheerfun.pixivic.basic.ad.mapper.AdvertisementMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/2/20 上午11:48
 * @description UserInfoProcessor
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Order(4)
public class AdvertisementProcessor {
    private final AdvertisementMapper advertisementMapper;
    private static List<Integer> randomList;
    private static Map<Integer, List<Advertisement>> advertisementMap;
    Random random;

    @PostConstruct
    public void init() {
        random = new Random(21);
        List<AdvertisementInfo> advertisementInfos = advertisementMapper.queryAllEnableAdvertisementInfo();
        //构造randomList
        advertisementInfos.forEach(e -> {
            for (int i = 0; i < e.getWeight(); i++) {
                randomList.add(e.getId());
            }
        });
        Collections.shuffle(randomList);
        //转换成Advertisement分组构造advertisementMap
        advertisementMap = advertisementInfos.stream().map(Advertisement::new).collect(Collectors.groupingBy(Advertisement::getAdId));
    }

}
