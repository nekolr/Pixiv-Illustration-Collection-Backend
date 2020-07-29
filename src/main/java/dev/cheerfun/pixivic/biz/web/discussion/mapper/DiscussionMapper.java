package dev.cheerfun.pixivic.biz.web.discussion.mapper;

import dev.cheerfun.pixivic.biz.web.discussion.po.Discussion;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DiscussionMapper {
    Integer createDiscussion(Discussion discussion);
}
