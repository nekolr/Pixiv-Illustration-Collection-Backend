package dev.cheerfun.pixivic.biz.ad.service;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.biz.ad.domain.Advertisement;
import dev.cheerfun.pixivic.biz.ad.mapper.AdvertisementMapper;
import dev.cheerfun.pixivic.biz.ad.po.AdvertisementInfo;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.util.aop.RequestParamUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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
    private List<Integer> randomList;
    private Map<String, List<Advertisement>> advertisementMap;
    private List<Advertisement> advertisementList;
    private final AdvertisementMapper advertisementMapper;
    private final RequestParamUtil requestParamUtil;
    BloomFilter<String> bloomFilter;

    @PostConstruct
    public void init() {
        randomList = new ArrayList<>();
        List<AdvertisementInfo> advertisementInfos = advertisementMapper.queryAllEnableAdvertisementInfo();
        //构造randomList
        for (int i = 0; i < advertisementInfos.size(); i++) {
            for (int j = 0; j < advertisementInfos.get(i).getWeight(); j++) {
                randomList.add(i);
            }
        }
        Collections.shuffle(randomList);
        //构造advertisementList
        advertisementList = advertisementInfos.stream().map(Advertisement::new).collect(Collectors.toList());
        //转换成Advertisement分组构造advertisementMap
        advertisementMap = advertisementList.stream().collect(Collectors.groupingBy(e -> e.getArtistPreView().getName()));
        bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.US_ASCII), 1000000, 0.001);
    }

    public List<Advertisement> serveAds() {
        String identification;
        //先获取用户id
        Integer userId = requestParamUtil.queryUserIdFromAppContext();
        if (userId != null) {
            identification = String.valueOf(userId);
        } else {
            //如果没有则获取ip地址
            identification = String.valueOf(requestParamUtil.queryRealIp());
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int isAdd = random.nextInt(1000);
        //使用布隆过滤器查看是否投放过一次
        if (bloomFilter.mightContain(identification)) {
            //如果投放过 以一个较低的随机来投放
            if (userId == null ? isAdd < 120 : isAdd < ((int) AppContext.get().get(AuthConstant.PERMISSION_LEVEL) < PermissionLevel.VIP ? 85 : 75)) {
                //优化成取余
                Advertisement advertisement = advertisementList.get(randomList.get(isAdd % randomList.size()));
                return Collections.singletonList(advertisement);
            }
        } else {
            //如果没投放过 以一个较高的随机来投放 且将每个广告主的广告都投放一次
            if (isAdd < 500) {
                List<Advertisement> advertisementList = new ArrayList<>(advertisementMap.size());
                advertisementMap.forEach((k, v) -> {
                    advertisementList.add(v.get(0));
                });
                bloomFilter.put(identification);
                return advertisementList;
            }
        }
        return null;
    }

    @Scheduled(cron = "0 0 */12 * * ?")
    public synchronized void resetBloomFilter() {
        bloomFilter = null;
        bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.US_ASCII), 1000000, 0.001);
    }

}
