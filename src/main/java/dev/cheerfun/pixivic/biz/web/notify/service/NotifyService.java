package dev.cheerfun.pixivic.biz.web.notify.service;

import dev.cheerfun.pixivic.biz.notify.po.NotifyRemind;
import dev.cheerfun.pixivic.biz.notify.service.NotifyRemindService;
import dev.cheerfun.pixivic.biz.web.notify.mapper.NotifyBIZMapper;
import dev.cheerfun.pixivic.biz.web.notify.po.NotifyRemindSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/9 8:15 PM
 * @description NotifyService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NotifyService {
    private final NotifyBIZMapper notifyMapper;
    private final NotifyRemindService notifyRemindService;

    @Transactional
    public List<NotifyRemind> queryRemind(int userId, Integer type, long offset, int pageSize) {
        List<Integer> remindIdList = notifyMapper.queryRemind(userId, type, offset, pageSize);
        List<NotifyRemind> notifyReminds = notifyRemindService.queryRemindList(remindIdList);
        //将summary的未读减掉对应的
        long count = notifyReminds.stream().filter(e -> {
            Integer status = e.getStatus();
            if (status.compareTo(0) == 0) {
                //如果未读更新状态
                readRemind(e.getId());
                return true;
            }
            return false;
        }).count();
        if (count > 0) {
            updateRemindSummary(userId, type, count);
        }
        //将id加入移步刷新队列
        return notifyReminds;
    }

    @CacheEvict("remind")
    public void readRemind(Integer remindId) {
        notifyMapper.readRemind(remindId);
    }

    @CacheEvict(value = "remindSummary", key = "#userId")
    public void updateRemindSummary(Integer userId, Integer type, long count) {
        notifyMapper.updateRemindSummary(userId, type, count);

    }

    @Cacheable("remindSummary")
    public List<NotifyRemindSummary> queryRemindSummary(int userId) {
        return notifyMapper.queryRemindSummary(userId);
    }

    public Integer queryUnreadRemindsCount(int userId) {
        return queryRemindSummary(userId).stream().mapToInt(NotifyRemindSummary::getUnread).sum();
    }
}
