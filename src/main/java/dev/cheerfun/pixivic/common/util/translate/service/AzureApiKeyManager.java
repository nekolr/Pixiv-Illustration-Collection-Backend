package dev.cheerfun.pixivic.common.util.translate.service;

import dev.cheerfun.pixivic.common.util.translate.domain.AzureApiKey;
import dev.cheerfun.pixivic.common.util.translate.mapper.TranslateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/8/20 11:50 上午
 * @description AzureApiKey
 */
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AzureApiKeyManager {
    private List<AzureApiKey> availableList;
    private Integer availableListSize;
    private Random random;
    private List<AzureApiKey> unAvailableList;
    private final TranslateMapper translateMapper;
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock(false);
    final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    @PostConstruct
    public void init() {
        try {
            //初始化apikey
            log.info("开始初始化az翻译api管理类");
            random = new Random(21);
            //如果是一号，把所有key置为有效
            if (LocalDate.now().getDayOfMonth() == 1) {
                translateMapper.resetStatus();
            }
            availableList = translateMapper.queryAll();
            availableListSize = availableList.size();
            unAvailableList = new ArrayList<>();
            log.info("az翻译api管理类初始化完毕");
        } catch (Exception exception) {
            log.error("az翻译api管理类初始化失败");
        }

    }

    public AzureApiKey getKey() {
        readLock.lock();
        try {
            if (availableListSize > 0) {
                return availableList.get(random.nextInt(availableListSize));
            }
            return null;
        } finally {
            readLock.unlock();
        }
    }

    public void ban(AzureApiKey azureApiKey) {
        writeLock.lock();
        try {
            translateMapper.ban(azureApiKey.getId());
            availableList = translateMapper.queryAll();
            availableListSize = availableList.size();
        } finally {
            writeLock.unlock();
        }
    }

    public void invalid(AzureApiKey azureApiKey) {
        writeLock.lock();
        try {
            translateMapper.invalid(azureApiKey.getId());
            availableList = translateMapper.queryAll();
            availableListSize = availableList.size();
        } finally {
            writeLock.unlock();
        }
    }

}
