package dev.cheerfun.pixivic.biz.cibr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.cibr.constant.MilvusInfo;
import dev.cheerfun.pixivic.biz.cibr.po.IllustFeature;
import dev.cheerfun.pixivic.biz.cibr.secmapper.CBIRInitMapper;
import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/5/8 9:15 PM
 * @description CBIRInitService
 */
//@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CBIRInitService {
    private final CBIRInitMapper cbirInitMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final MilvusService milvusService;
    private final ObjectMapper objectMapper;
    private final ReverseImageSearchService reverseImageSearchService;
    private final ExecutorService crawlerExecutorService;
    private int illustIdFlag;

    public synchronized List<Integer> queryIllustIdList() {
        //获取50个画作
        List<Integer> illustIdList = cbirInitMapper.queryIllustIdList(illustIdFlag);
        //修改illustIdFlag
        illustIdFlag = illustIdList.get(illustIdList.size() - 1);
        return illustIdList;
    }

    //@PostConstruct
    public void init() {
       /* illustIdFlag = cbirInitMapper.queryLatest();
        log.info("-------------------------开始抽取画作特征入库");

        for (int i = 0; i < 20; i++) {
            crawlerExecutorService.submit(() -> {
                while (true) {
                    List<Integer> queryIllustIdList = queryIllustIdList();
                    List<Illustration> illustrations = reverseImageSearchService.queryIllustrationByIdList(queryIllustIdList);
                    illustrations.forEach(e -> {
                        try {
                            reverseImageSearchService.illustFeatureExtraction(e);
                            log.info("画作：" + e.getId() + "特征抽取完毕");
                            cbirInitMapper.updateImageSync(e.getId());
                        } catch (IOException | InterruptedException ioException) {
                            ioException.printStackTrace();
                        }
                    });

                }
            });
        }
        log.info("-------------------------画作特征入库完毕");*/
        //读取sync表查找没提取特征的画作
        //下载画作
        //提取特征
        //入库
        //stringRedisTemplate.opsForValue().set("record","0");
        String record = stringRedisTemplate.opsForValue().get("temp");
        Long flag;
        if (record == null) {
            flag = 0L;
        } else {
            flag = Long.parseLong(record);
        }
        //从数据库5000 5000的查 每次修改完就修改record的值
        Boolean f = true;
        while (f) {
            List<IllustFeature> illustFeatures = cbirInitMapper.queryVectorList(flag);
            if (illustFeatures.size() == 0) {
                return;
            }
            flag = illustFeatures.get(illustFeatures.size() - 1).getIllustId();
            int size = illustFeatures.size();
            for (int i = 0; i < size; i++) {
                try {
                    if (illustFeatures.get(i).getFeature() != null) {
                        Float[] floats = objectMapper.readValue(illustFeatures.get(i).getFeature(), Float[].class);
                        Integer sync = cbirInitMapper.isSync(illustFeatures.get(i).getIllustId(), illustFeatures.get(i).getImagePage());
                        if (sync == null) {
                            milvusService.saveIllustFeatureToMilvus(illustFeatures.get(i).getIllustId(), Collections.singletonList(Arrays.asList(floats)), MilvusInfo.COLLECTION);
                            cbirInitMapper.finishSyncToMilvus(illustFeatures.get(i).getIllustId(), illustFeatures.get(i).getImagePage());
                        }
                    }
                    stringRedisTemplate.opsForValue().set("temp", String.valueOf(illustFeatures.get(illustFeatures.size() - 1).getIllustId()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        milvusService.flush(MilvusInfo.COLLECTION);
    }

    public List<Illustration> queryIllustrationByIdList(List<Integer> illustId) {
        return illustId.stream().parallel().map(reverseImageSearchService::queryIllustrationByIdFromDb).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
