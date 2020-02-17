package dev.cheerfun.pixivic.biz.web.comment.service;

import dev.cheerfun.pixivic.biz.web.comment.dto.Like;
import dev.cheerfun.pixivic.biz.web.comment.exception.CommentException;
import dev.cheerfun.pixivic.biz.web.comment.mapper.CommentMapper;
import dev.cheerfun.pixivic.biz.web.comment.po.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
        //
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
        List<Comment> result = new ArrayList<>();
        Map<Integer, List<Comment>>[] mayByParentId = new Map[]{null};
        List<Object> isLikedList = stringRedisTemplate.executePipelined((RedisCallback<String>) redisConnection -> {
            //上层id分组
            mayByParentId[0] = comments.stream().collect(Collectors.groupingBy(e -> {
                //顶级评论引用列表
                if (e.getParentId() == 0) {
                    result.add(e);
                }
                StringRedisConnection stringRedisConnection = (StringRedisConnection) redisConnection;
                stringRedisConnection.sIsMember(likeRedisPre + userId, String.valueOf(e.toStringForQueryLike()));
                return e.getParentId();
            }));
            return null;
        });
        int index = comments.size();
        for (int i = 0; i < index; i++) {
            Comment comment = comments.get(i);
            comment.setIsLike((Boolean) isLikedList.get(i));
            //拼成树形
            if (comment.getParentId() == 0) {
                comment.setSubCommentList(mayByParentId[0].get(comment.getId()));
            }
        }
        //最新消息逆序
        result.sort(Comparator.comparingInt(Comment::getId));
        return result;
    }

    @Cacheable(value = "comments")
    public List<Comment> queryCommentList(String appType, Integer appId) {
        return commentMapper.pullComment(appType, appId);
    }
}
