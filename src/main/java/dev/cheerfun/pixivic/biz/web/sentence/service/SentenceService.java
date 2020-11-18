package dev.cheerfun.pixivic.biz.web.sentence.service;

import dev.cheerfun.pixivic.biz.web.sentence.mapper.SentenceMapper;
import dev.cheerfun.pixivic.biz.web.sentence.po.Sentence;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/30 9:19 PM
 * @description SentenceService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SentenceService {
    private final SentenceMapper sentenceMapper;
    private Integer sentenceListSize;

    @PostConstruct
    public void init() {
        //取出数量
        sentenceListSize = querySentenceListSize();
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
