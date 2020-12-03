package dev.cheerfun.pixivic.biz.ad.aop;

import dev.cheerfun.pixivic.biz.ad.domain.Advertisement;
import dev.cheerfun.pixivic.biz.ad.mapper.AdvertisementMapper;
import dev.cheerfun.pixivic.biz.ad.po.AdvertisementInfo;
import dev.cheerfun.pixivic.biz.ad.service.AdvertisementService;
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
import java.util.concurrent.ThreadLocalRandom;
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
    private final AdvertisementService advertisementService;

    @Pointcut(value = "@annotation(dev.cheerfun.pixivic.biz.ad.annotation.WithAdvertisement)||@within(dev.cheerfun.pix" +
            "ivic.biz.ad.annotation.WithAdvertisement)")
    public void pointCut() {
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
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Result<List> body = (Result<List>) ((ResponseEntity) responseEntity).getBody();
        List bodyData = body.getData();
        if (bodyData.size() > 0) {
            //随机决定是否插入
            Advertisement advertisement = advertisementService.serveAds();
            if (advertisement != null) {
                List data = new ArrayList(bodyData.size() + 1);
                data.addAll(bodyData);
                data.add(advertisement);
                body.setData(data);
            }
        }
    }

}
