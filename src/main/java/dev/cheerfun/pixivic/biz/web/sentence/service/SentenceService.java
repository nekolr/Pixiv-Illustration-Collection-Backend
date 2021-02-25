package dev.cheerfun.pixivic.biz.web.sentence.service;

import dev.cheerfun.pixivic.biz.web.sentence.mapper.SentenceMapper;
import dev.cheerfun.pixivic.biz.web.sentence.po.Sentence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/30 9:19 PM
 * @description SentenceService
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SentenceService {
    private final SentenceMapper sentenceMapper;
    private Integer sentenceListSize;

    @PostConstruct
    public void init() {
        try {
            log.info("开始初始化签到语句服务");
            //取出数量
            sentenceListSize = querySentenceListSize();
        } catch (Exception e) {
            log.error("初始化签到语句服务失败");
            e.printStackTrace();
        }
        log.info("初始化签到语句服务成功");

    }

    public Sentence queryRandomSentence() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return querySentenceById(random.nextInt(sentenceListSize));
    }

    @Cacheable("sentence")
    public Sentence querySentenceById(Integer sentenceId) {
        return sentenceMapper.querySentenceById(sentenceId);
    }

    public Integer querySentenceListSize() {
        return sentenceMapper.querySentenceListSize();
    }
}
