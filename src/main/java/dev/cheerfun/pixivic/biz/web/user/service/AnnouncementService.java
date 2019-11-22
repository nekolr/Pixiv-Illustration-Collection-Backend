package dev.cheerfun.pixivic.biz.web.user.service;

import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.biz.web.user.mapper.AnnouncementMapper;
import dev.cheerfun.pixivic.biz.web.user.model.Announcement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void send(Announcement announcement) {
        int result = announcementMapper.insert(announcement);
        if (result != 1) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "公告发布失败");
        }
    }

    public List<Announcement> query(int userId, String date) {
        List<Announcement> announcements = announcementMapper.query(date);
        if (announcements.size() == 0) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "暂无系统公告");
        }
        return announcements;
    }
}
