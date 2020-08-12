package dev.cheerfun.pixivic.biz.web.announcement.service;

import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.biz.web.announcement.mapper.AnnouncementMapper;
import dev.cheerfun.pixivic.biz.web.announcement.po.Announcement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public void send(Announcement announcement) {
        int result = announcementMapper.insert(announcement);
        if (result != 1) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "公告发布失败");
        }
    }

    public List<Announcement> queryByDate(String date) {
        List<Announcement> announcements = loadList(queryIdByDate(date));
        if (announcements.size() == 0) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "暂无系统公告");
        }
        return announcements;
    }

    @Cacheable("dailyAnnouncements")
    public List<Integer> queryIdByDate(String date) {
        return announcementMapper.queryByDate(date);
    }

    public List<Announcement> queryList(Integer page, Integer pageSize) {
        List<Announcement> announcements = loadList(queryIdList(page, pageSize));
        if (announcements.size() == 0) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "暂无系统公告");
        }
        return announcements;
    }

    @Cacheable("announcementList")
    public List<Integer> queryIdList(Integer page, Integer pageSize) {
        return announcementMapper.queryList((page - 1) * pageSize, pageSize);
    }

    @Cacheable("announcementListCount")
    public Integer queryListCount() {
        return announcementMapper.queryListCount();
    }

    @Cacheable("announcements")
    public Announcement queryById(Integer id) {
        return announcementMapper.queryById(id);
    }

    public List<Announcement> loadList(List<Integer> idList) {
        return idList.stream().map(this::queryById).collect(Collectors.toList());
    }

}
