package dev.cheerfun.pixivic.web.user.service;

import dev.cheerfun.pixivic.web.user.exception.BusinessException;
import dev.cheerfun.pixivic.web.user.mapper.AnnouncementMapper;
import dev.cheerfun.pixivic.web.user.model.Announcement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/24 8:40
 * @description AnnouncementService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AnnouncementService {
    private final AnnouncementMapper announcementMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final String userReadAnnouncementPre = "u:ra:";

    public void send(Announcement announcement) {
        int result = announcementMapper.insert(announcement);
        if (result != 1) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "公告发布失败");
        }
    }

    public List<Announcement> query(int userId, String date) {
        List<Announcement> announcements = announcementMapper.query(date);
        Set<String> difference = stringRedisTemplate.opsForSet().difference(userReadAnnouncementPre + userId, announcements.stream().map(a -> String.valueOf(a.getAnnouncementId())).collect(Collectors.toList()));
        if (difference != null) {
            stringRedisTemplate.opsForSet().differenceAndStore(userReadAnnouncementPre + userId, announcements.stream().map(a -> String.valueOf(a.getAnnouncementId())).collect(Collectors.toList()), userReadAnnouncementPre + userId);
            List<Announcement> result = announcements.stream().filter(a -> difference.contains(String.valueOf(a.getAnnouncementId()))).collect(Collectors.toList());
            return result;
        }
        throw new BusinessException(HttpStatus.NOT_FOUND, "暂无系统公告");
    }
}
