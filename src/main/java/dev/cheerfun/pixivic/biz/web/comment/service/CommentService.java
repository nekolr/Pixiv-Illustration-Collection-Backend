package dev.cheerfun.pixivic.biz.web.comment.service;

import dev.cheerfun.pixivic.biz.web.comment.dto.Like;
import dev.cheerfun.pixivic.biz.web.comment.exception.CommentException;
import dev.cheerfun.pixivic.biz.web.comment.mapper.CommentMapper;
import dev.cheerfun.pixivic.biz.web.comment.po.Comment;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
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
    private final ExecutorService saveToDBExecutorService;
    private Map<String, Integer> waitForUpdateApp = new ConcurrentHashMap<>(1000 * 1000);

    @PostConstruct
    public void init() {
        dealWaitForUpdateApp();
    }

    @Caching(evict = {
            @CacheEvict(value = "comments", key = "#comment.appType+#comment.appId"),
            @CacheEvict(value = "topCommentsCount", key = "#comment.appType+#comment.appId")
    })
    public void pushComment(Comment comment) {
        commentMapper.pushComment(comment);
        if (comment.getParentId().compareTo(0) == 0) {
            waitForUpdateApp.put(comment.getAppType() + ":" + comment.getAppId(), 1);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void likeComment(Like like, int userId) {
        Long add = stringRedisTemplate.opsForSet().add(RedisKeyConstant.LIKE_REDIS_PRE + userId, like.toString());
        if (add != null && add != 0L) {
            stringRedisTemplate.opsForHash().increment(RedisKeyConstant.LIKE_COUNT_MAP_REDIS_PRE, like.toString(), 1);
        } else {
            throw new CommentException(HttpStatus.BAD_REQUEST, "用户评论点赞关系请求错误");
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelLikeComment(int userId, Like like) {
        Long remove = stringRedisTemplate.opsForSet().remove(RedisKeyConstant.LIKE_REDIS_PRE + userId, like.toString());
        if (remove != null && remove != 0L) {
            stringRedisTemplate.opsForHash().increment(RedisKeyConstant.LIKE_COUNT_MAP_REDIS_PRE, like.toString(), -1);
        } else {
            throw new CommentException(HttpStatus.BAD_REQUEST, "用户评论点赞关系请求错误");
        }
    }

    public List<Comment> pullComment(String appType, Integer appId, Integer page, Integer pageSize) {
        List<Comment> comments = pullComment(appType, appId);
        return comments.stream().skip(pageSize * (page - 1))
                .limit(pageSize).collect(Collectors.toList());
    }

    public List<Comment> pullComment(String appType, Integer appId) {
        List<Comment> comments = queryCommentList(appType, appId);
        if (comments.size() == 0) {
            return comments;
        }
        List<Comment> result = new ArrayList<>();
        Map<Integer, List<Comment>>[] mayByParentId = new Map[]{null};
        //拼接是否点赞
        List<Object> isLikedList;
        if (AppContext.get() != null && AppContext.get().get(AuthConstant.USER_ID) != null) {
            isLikedList = comments.stream().map(e -> {
                //拼接点赞数
                e.setLikedCount(Integer.valueOf((String) stringRedisTemplate.opsForHash().get(RedisKeyConstant.LIKE_COUNT_MAP_REDIS_PRE, e.getAppType() + ':' + e.getAppId() + ":" + e.getId())));
                return stringRedisTemplate.opsForSet().isMember(RedisKeyConstant.LIKE_REDIS_PRE + AppContext.get().get(AuthConstant.USER_ID), String.valueOf(e.toStringForQueryLike()));
            }).collect(Collectors.toList());
        } else {
            isLikedList = comments.stream().map(e -> {
                e.setLikedCount(Integer.valueOf((String) stringRedisTemplate.opsForHash().get(RedisKeyConstant.LIKE_COUNT_MAP_REDIS_PRE, e.getAppType() + ':' + e.getAppId() + ":" + e.getId())));
                return false;
            }).collect(Collectors.toList());
        }
        //顶级评论
        mayByParentId[0] = comments.stream().collect(Collectors.groupingBy(e -> {
            //顶级评论引用列表
            if (e.getParentId() == 0) {
                result.add(e);
            }
            return e.getParentId();
        }));

        int index = comments.size();
        for (int i = 0; i < index; i++) {
            Comment comment = comments.get(i);
            comment.setIsLike((Boolean) isLikedList.get(i));
            //拼成树形
            if (comment.getParentId() == 0) {
                comment.setSubCommentList(mayByParentId[0] != null ? mayByParentId[0].get(comment.getId()) : null);
            }
        }
        //最新消息逆序
        result.sort(Comparator.comparingInt(Comment::getId));
        return result;
    }
    @Cacheable(value = "comments", key = "#appType+#appId")
    public List<Comment> queryCommentList(String appType, Integer appId) {
        return commentMapper.pullComment(appType, appId);
    }

    @Cacheable(value = "topCommentsCount", key = "#appType+#appId")
    public Integer queryTopCommentCount(String appType, Integer appId) {
        return commentMapper.queryTopCommentCount(appType, appId);
    }

    //异步统计评论信息
    public void dealWaitForUpdateApp() {
        saveToDBExecutorService.submit(() -> {
            while (true) {
                try {
                    if (!waitForUpdateApp.isEmpty()) {
                        ArrayList<String> appList = new ArrayList<>(waitForUpdateApp.keySet());
                        waitForUpdateApp.clear();
                        appList.forEach(e -> {
                            String[] split = e.split(":");
                            commentMapper.countCommentSummary(split[0], split[1]);
                        });
                    }
                    Thread.sleep(1000 * 60);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
