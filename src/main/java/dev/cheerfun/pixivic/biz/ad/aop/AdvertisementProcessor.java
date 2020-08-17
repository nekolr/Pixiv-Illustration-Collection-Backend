package dev.cheerfun.pixivic.biz.ad.aop;

import dev.cheerfun.pixivic.biz.ad.domain.Advertisement;
import dev.cheerfun.pixivic.biz.ad.mapper.AdvertisementMapper;
import dev.cheerfun.pixivic.biz.ad.po.AdvertisementInfo;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.CompletableFuture;
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
@Order(3)
public class AdvertisementProcessor {
    private static List<Integer> randomList;
    private static Map<Integer, List<Advertisement>> advertisementMap;
    private final AdvertisementMapper advertisementMapper;
    private final Random random = new Random(21);

    @Pointcut(value = "@annotation(dev.cheerfun.pixivic.biz.ad.annotation.WithAdvertisement)||@within(dev.cheerfun.pix" +
            "ivic.biz.ad.annotation.WithAdvertisement)")
    public void pointCut() {
    }

    @PostConstruct
    public void init() {
        randomList = new ArrayList<>();
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

    @Around(value = "pointCut()")
    public Object withAD(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        if (result instanceof ResponseEntity) {
            insertAD(result);
        } else if (result instanceof CompletableFuture) {
            ((CompletableFuture) result).thenAccept(e -> {
                insertAD(e);
            });
        }
        return result;
    }

    public void insertAD(Object responseEntity) {
        Result<List> body = (Result<List>) ((ResponseEntity) responseEntity).getBody();
        List bodyData = body.getData();
        if (bodyData.size() > 0) {
            List data = new ArrayList(bodyData.size() + 1);
            data.addAll(bodyData);
            //随机决定是否插入
            int isAdd = random.nextInt(1000);
            if (isAdd < 99) {
                //如果插入则根据权重选一个广告插入
                int i = random.nextInt(randomList.size());
                Advertisement advertisement = advertisementMap.get(randomList.get(i)).get(0);
                data.add(advertisement);
                body.setData(data);
            }
        }
    }

}
