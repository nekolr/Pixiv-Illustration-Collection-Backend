package dev.cheerfun.pixivic.biz.comment.service;

import dev.cheerfun.pixivic.biz.comment.dto.Like;
import dev.cheerfun.pixivic.biz.comment.exception.CommentException;
import dev.cheerfun.pixivic.biz.comment.mapper.CommentMapper;
import dev.cheerfun.pixivic.biz.comment.po.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/12/09 20:38
 * @description CommentService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommentService {
    private final StringRedisTemplate stringRedisTemplate;
    private final CommentMapper commentMapper;
    private final String likeRedisPre = "u:l:c:";
    private final String likeCountMapRedisPre = "c:lcm";//+appType:appId

    @CacheEvict(value = "comments", allEntries = true)
    public void pushComment(Comment comment) {
        commentMapper.pushComment(comment);
    }

    @Transactional
    public void likeComment(Like like, int userId) {
        Long add = stringRedisTemplate.opsForSet().add(likeRedisPre + userId, like.toString());
        if (add != null && add != 0L) {
            stringRedisTemplate.opsForHash().increment(likeCountMapRedisPre, like.toString(), 1);
        } else {
            throw new CommentException(HttpStatus.BAD_REQUEST, "用户评论点赞关系请求错误");
        }

    }

    @Transactional
    public void cancelLikeComment(int userId, Like like) {
        Long remove = stringRedisTemplate.opsForSet().remove(likeRedisPre + userId, like.toString());
        if (remove != null && remove != 0L) {
            stringRedisTemplate.opsForHash().increment(likeCountMapRedisPre, like.toString(), -1);
        } else {
            throw new CommentException(HttpStatus.BAD_REQUEST, "用户评论点赞关系请求错误");
        }
    }

    public List<Comment> pullComment(String appType, Integer appId, int userId) {
        List<Comment> comments = queryCommentList(appType, appId);
        //拼接是否点赞
        Set<String> commentSet = stringRedisTemplate.opsForSet().members(likeRedisPre + userId);

        Map<Integer, List<Comment>> mayByParentId = null;
        List<Comment> result = new ArrayList<>();
        if (commentSet != null) {
            mayByParentId = comments.stream().collect(Collectors.groupingBy(e -> {
                        if (e.getParentId() == 0) {
                            result.add(e);
                        }
                        e.setIsLike(commentSet.contains(e.toStringForQueryLike()));
                        return e.getParentId();
                    }
            ));
        }
        //拼成树形
        for (Comment comment : result) {
            comment.setSubCommentList(mayByParentId.get(comment.getId()));
        }
        return result;
    }

    @Cacheable(value = "comments")
    public List<Comment> queryCommentList(String appType, Integer appId) {
        return commentMapper.pullComment(appType, appId);
    }
}
