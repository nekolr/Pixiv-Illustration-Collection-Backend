package dev.cheerfun.pixivic.biz.web.cibr.service;

import dev.cheerfun.pixivic.biz.web.cibr.mapper.CBIRInitMapper;
import dev.cheerfun.pixivic.biz.web.illust.service.IllustrationBizService;
import dev.cheerfun.pixivic.common.po.Illustration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

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
    private final IllustrationBizService illustrationBizService;
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

    @PostConstruct
    public void init() {
        illustIdFlag = cbirInitMapper.queryLatest();
        log.info("-------------------------开始抽取画作特征入库");

        for (int i = 0; i < 50; i++) {
            crawlerExecutorService.submit(() -> {
                while (true) {
                    List<Integer> queryIllustIdList = queryIllustIdList();
                    List<Illustration> illustrations = illustrationBizService.queryIllustrationByIdList(queryIllustIdList);
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
        log.info("-------------------------画作特征入库完毕");
        //读取sync表查找没提取特征的画作
        //下载画作
        //提取特征
        //入库

    }
}
