package dev.cheerfun.pixivic.biz.web.reward.service;

import dev.cheerfun.pixivic.basic.event.constant.ActionType;
import dev.cheerfun.pixivic.basic.event.constant.ObjectType;
import dev.cheerfun.pixivic.biz.credit.po.CreditHistory;
import dev.cheerfun.pixivic.biz.web.collection.po.Collection;
import dev.cheerfun.pixivic.biz.web.collection.service.CollectionService;
import dev.cheerfun.pixivic.biz.web.comment.po.Comment;
import dev.cheerfun.pixivic.biz.web.comment.service.CommentService;
import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.biz.web.common.po.User;
import dev.cheerfun.pixivic.biz.web.discussion.po.Discussion;
import dev.cheerfun.pixivic.biz.web.discussion.service.DiscussionService;
import dev.cheerfun.pixivic.biz.web.reward.mapper.Rewardmapper;
import dev.cheerfun.pixivic.biz.web.reward.po.Reward;
import dev.cheerfun.pixivic.biz.web.user.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/15 7:31 PM
 * @description RewardService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RewardService {
    private final Rewardmapper rewardmapper;
    private final CollectionService collectionService;
    private final CommonService commonService;
    private final CommentService commentService;
    private final DiscussionService discussionService;

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "rewardIdList", key = "#reward.appType+#reward.appId"),
            @CacheEvict(value = "rewardCount", key = "#reward.appType+#reward.appId")
    })
    public void pushReward(Reward reward) {
        //校验用户积分是否足够
        if (reward.getPrice() != null && reward.getPrice() > 0 && checkUserPoint(reward.getFrom(), reward.getPrice())) {
            //扣除积分
            commonService.modifyUserPoint(reward.getFrom(), -reward.getPrice());
            //增加扣分记录
            insertCreditLog(new CreditHistory(null, reward.getFrom(), reward.getAppType(), reward.getAppId(), ActionType.REWARD, 0, reward.getPrice(), "打赏扣除", null));
            //找到目标对象的拥有者
            Integer userId = queryUserIdByAppTypeAndAppId(reward.getAppType(), reward.getAppId());
            if (userId != null && userId.compareTo(reward.getFrom()) != 0) {
                //给目标对象加分
                commonService.modifyUserPoint(reward.getFrom(), reward.getPrice());
                reward.setTo(userId);
                //reward增加记录
                rewardmapper.insertReward(reward);
                //增加加分记录
                insertCreditLog(new CreditHistory(null, reward.getTo(), reward.getAppType(), reward.getAppId(), ActionType.REWARD, 1, reward.getPrice(), "打赏加分", null));
                //更新reward汇总表
                rewardmapper.updateSummary(reward);
                return;
            }
        }
        throw new BusinessException(HttpStatus.BAD_REQUEST, "打赏失败");
    }

    @Caching(evict = {
            @CacheEvict(value = "userRecentlyCreditHistoryList", key = "#creditHistory.userId")
    })
    public void insertCreditLog(CreditHistory creditHistory) {
        rewardmapper.insertCreditLog(creditHistory);
    }

    private Boolean checkUserPoint(Integer from, Integer price) {
        User user = commonService.queryUser(from);
        return user.getStar() >= price;
    }

    protected Integer queryUserIdByAppTypeAndAppId(String appType, Integer appId) {
        switch (appType) {
            case ObjectType.COLLECTION:
                Collection collection = collectionService.queryCollectionByIdFromDb(appId);
                return collection.getUserId();
            case ObjectType.COMMENT:
                Comment comment = commentService.queryCommentById(appId);
                return comment.getReplyFrom();
            case ObjectType.DISCUSSION:
                Discussion discussion = discussionService.queryByIdFromDb(appId);
                return discussion.getUserId();
            default:
                return 0;
        }

    }

    @Cacheable(value = "rewardIdList", key = "#appType+#appId")
    public List<Integer> pullRewardId(String appType, int appId) {
        return rewardmapper.pullReward(appType, appId);
    }

    @Cacheable("reward")
    public Reward queryRewardById(Integer rewardId) {
        return rewardmapper.queryRewardById(rewardId);
    }

    public List<Reward> pullReward(String appType, int appId, Integer page, Integer pageSize) {
        return pullRewardId(appType, appId).stream().skip(pageSize * (page - 1))
                .limit(pageSize).map(this::queryRewardById).collect(Collectors.toList());
    }

    @Cacheable(value = "rewardCount", key = "#appType+#appId")
    public Integer pullRewardCount(String appType, int appId) {
        return rewardmapper.pullRewardCount(appType, appId);
    }
}
