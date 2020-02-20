package dev.cheerfun.pixivic.basic.ad.aop;

import dev.cheerfun.pixivic.basic.ad.domain.Advertisement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
    private static List<Integer> randomList;
    private static Map<Integer, Advertisement> advertisementMap;
    Random random;

    @PostConstruct
    public void init() {
        random = new Random(21);

    }

}
