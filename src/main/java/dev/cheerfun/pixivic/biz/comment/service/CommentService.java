package dev.cheerfun.pixivic.biz.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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
    private final String likeRedisPre = "u:l:";//+appType:appId
    private final String likeCountRedisPre = "l:c:";

    //点赞
    public void like() {
    }

    //取消点赞
    public void cancelLike() {
    }

    private void likeOperation(int userId, int commentId, int increment) {
    }

}
