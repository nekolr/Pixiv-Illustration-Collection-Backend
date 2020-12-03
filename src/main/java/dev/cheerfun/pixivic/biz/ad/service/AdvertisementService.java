package dev.cheerfun.pixivic.biz.ad.service;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import dev.cheerfun.pixivic.biz.ad.domain.Advertisement;
import dev.cheerfun.pixivic.biz.ad.mapper.AdvertisementMapper;
import dev.cheerfun.pixivic.biz.ad.po.AdvertisementInfo;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.constant.RequestParamType;
import dev.cheerfun.pixivic.common.util.aop.RequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/12/3 8:12 AM
 * @description AdvertisementService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdvertisementService {
    private static List<Integer> randomList;
    private static Map<Integer, List<Advertisement>> advertisementMap;
    private final AdvertisementMapper advertisementMapper;
    private final RequestUtil requestUtil;
    BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.US_ASCII), 1000000, 0.001);

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

    public Advertisement serveAds() {
        String identification;
        //先获取用户id
        Integer userId = requestUtil.queryUserIdFromAppContext();
        if (userId != null) {
            identification = String.valueOf(userId);
        } else {
            //如果没有则获取ip地址
            identification = String.valueOf(requestUtil.queryRealIp());
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int isAdd = random.nextInt(1000);
        //使用布隆过滤器查看是否投放过一次
        if (bloomFilter.mightContain(identification)) {
            //如果投放过 以一个较低的随机来投放
            if (isAdd < 50) {
                int i = random.nextInt(randomList.size());
                Advertisement advertisement = advertisementMap.get(randomList.get(i)).get(0);
                return advertisement;
            }
        } else {
            //如果没投放过 以一个较高的随机来投放
            if (isAdd < 120) {
                int i = random.nextInt(randomList.size());
                Advertisement advertisement = advertisementMap.get(randomList.get(i)).get(0);
                bloomFilter.put(identification);
                return advertisement;
            }
        }
        return null;
    }

}
