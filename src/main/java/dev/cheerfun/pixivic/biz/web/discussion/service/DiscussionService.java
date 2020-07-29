package dev.cheerfun.pixivic.biz.web.discussion.service;

import dev.cheerfun.pixivic.biz.web.discussion.mapper.DiscussionMapper;
import dev.cheerfun.pixivic.biz.web.discussion.po.Discussion;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
    public Boolean createDiscussion(Discussion discussionDTO, Integer userId) {
        Discussion discussion = new Discussion(discussionDTO.getSectionId(), discussionDTO.getTitle(), discussionDTO.getContent(), userId, discussionDTO.getUsername(), discussionDTO.getTagList(), LocalDateTime.now());
        if (discussionMapper.createDiscussion(discussion).compareTo(1) == 0) {
            //发布消息
            return true;
        }
        return false;
    }

    //点赞/踩
    public Boolean upOrDown() {
        return true;
    }

    //查看详情

    //删除

    //5分钟内更新

    //根据板块分页查询

    //

}
