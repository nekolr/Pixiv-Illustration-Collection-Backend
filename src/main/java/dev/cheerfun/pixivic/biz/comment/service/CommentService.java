package dev.cheerfun.pixivic.biz.comment.service;

import dev.cheerfun.pixivic.biz.comment.dto.Like;
import dev.cheerfun.pixivic.biz.comment.mapper.CommentMapper;
import dev.cheerfun.pixivic.biz.comment.po.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void pushComment(Comment comment) {
        commentMapper.pushComment(comment);
    }

    @Transactional
    public void likeComment(Like like, int userId) {
        stringRedisTemplate.opsForSet().add(likeRedisPre, like.toString());
        stringRedisTemplate.opsForHash().increment(likeCountMapRedisPre, like.toString(), 1);
    }

    @Transactional
    public void cancelLikeComment(int userId, Like like) {
        stringRedisTemplate.opsForSet().remove(likeRedisPre, like.toString());
        stringRedisTemplate.opsForHash().increment(likeCountMapRedisPre, like.toString(), -1);
    }


}
