package dev.cheerfun.pixivic.biz.notify.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.biz.notify.mapper.NotifyMapper;
import dev.cheerfun.pixivic.biz.notify.po.Actor;
import dev.cheerfun.pixivic.biz.notify.po.NotifyBanSetting;
import dev.cheerfun.pixivic.biz.notify.po.NotifyRemind;
import dev.cheerfun.pixivic.biz.notify.po.NotifySettingConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/12/17 10:55
 * @description NotifyRemindService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NotifyRemindService {
    private final NotifyMapper notifyMapper;
    private final ObjectMapper objectMapper;

    @Cacheable("remind")
    public NotifyRemind queryRemindById(Integer remindId) {
        NotifyRemind notifyRemind = notifyMapper.queryNotifyRemindById(remindId);
        notifyRemind.setActors(objectMapper.convertValue(notifyRemind.getActors(), new TypeReference<List<Actor>>() {
        }));
        return notifyRemind;
    }

    @CacheEvict(value = "remind", key = "#notifyRemind.id")
    public void updateRemindActorAndCreateDate(NotifyRemind notifyRemind) {
        notifyMapper.updateRemindActorAndCreateDate(notifyRemind);
    }

    public List<NotifyRemind> queryRemindList(List<Integer> remindIdList) {
        return remindIdList.stream().map(this::queryRemindById).collect(Collectors.toList());
    }

    public List<NotifyRemind> queryRecentlyRemind(Integer recipientId, Integer type, LocalDateTime localDateTime) {
        return queryRemindList(notifyMapper.queryRecentlyRemind(recipientId, type, localDateTime));
    }

    public NotifyBanSetting queryUserBanSetting(Integer userId) {
        return notifyMapper.queryUserBanSetting(userId);
    }

    public List<NotifySettingConfig> queryNotifySettingConfig() {
        return notifyMapper.queryNotifySettingConfig();
    }

    public Integer insertNotifyRemind(NotifyRemind notifyRemind) {
        return notifyMapper.insertNotifyRemind(notifyRemind);
    }

    @CacheEvict(value = "remindSummary", key = "#notifyRemind.recipientId")
    public void updateRemindSummary(NotifyRemind notifyRemind) {
        notifyMapper.updateRemindSummary(notifyRemind);
    }

    //消息已读使用队列来移步更新，同步更新内存 因此不需要清理缓存

}
