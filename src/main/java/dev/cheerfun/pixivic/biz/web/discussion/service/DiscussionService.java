package dev.cheerfun.pixivic.biz.web.discussion.service;

import dev.cheerfun.pixivic.biz.web.discussion.mapper.DiscussionMapper;
import dev.cheerfun.pixivic.biz.web.discussion.po.Discussion;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/7/28 3:25 下午
 * @description DiscussionService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DiscussionService {
    private final DiscussionMapper discussionMapper;

    //新建
    @Transactional
    @CacheEvict(value = "sectionDiscussionCount", key = "#discussionDTO.sectionId")
    public Boolean createDiscussion(Discussion discussionDTO, Integer userId) {
        Discussion discussion = new Discussion(discussionDTO.getSectionId(), discussionDTO.getTitle(), discussionDTO.getContent(), userId, discussionDTO.getUsername(), discussionDTO.getTagList(), LocalDateTime.now());
        if (discussionMapper.createDiscussion(discussion).compareTo(1) == 0) {
            //总数+1
            discussionMapper.decrSectionDiscussionCount(discussion.getSectionId());
            //发布消息
            return true;
        }
        return false;
    }

    //点赞/踩
    @Transactional
    public Boolean upOrDown(Integer userId, Integer discussionId, Integer option) {
        //新增关系
        Integer result = discussionMapper.createOption(userId, discussionId, option);
        //更新total
        if (result == 1) {
            discussionMapper.updateDiscussionTotalUpAndDown(discussionId, option);
        }
        return true;
    }

    //详细
    @Cacheable("discussion")
    public Discussion queryById(Integer discussionId) {
        return discussionMapper.queryById(discussionId);
    }

    //更新最后回复时间
    public Boolean updateSort(Integer discussionId) {
        return discussionMapper.updateSort(discussionId) == 1;
    }

    //查看缩略列表
    public List<Discussion> queryList(Integer sectorId, Integer page, Integer pageSize) {
        List<Integer> idList = discussionMapper.queryList(sectorId, (page - 1) * pageSize, pageSize);
        return idList.stream().map(e -> queryById(e)).collect(Collectors.toList());
    }

    //查看缩略列表总数
    @Cacheable(value = "sectionDiscussionCount")
    public Integer queryListCount(Integer sectionId) {
        return discussionMapper.queryListCount(sectionId);
    }

    //搜索
    public List<Discussion> search() {
        return null;
    }

    //删除
    @Caching(evict = {
            @CacheEvict(value = "discussion", key = "#discussionId"),
            @CacheEvict(value = "sectionDiscussionCount", allEntries = true)
    })
    public Boolean deleteDiscussion(Integer userId, Integer discussionId) {
        return discussionMapper.deleteDiscussion(userId, discussionId) == 1;
    }

    //5分钟内更新
    @Caching(evict = {
            @CacheEvict(value = "discussion", key = "#discussion.id"),
            @CacheEvict(value = "sectionDiscussionCount", allEntries = true)
    })
    public Boolean updateDiscussion(Integer userId, Discussion discussion) {
        discussion.setUserId(userId);
        return discussionMapper.updateDiscussion(discussion) == 1;
    }

}
