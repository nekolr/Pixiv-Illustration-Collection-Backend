package dev.cheerfun.pixivic.biz.notify.customer;

import dev.cheerfun.pixivic.biz.event.constant.ObjectType;
import dev.cheerfun.pixivic.biz.event.domain.Event;
import dev.cheerfun.pixivic.biz.notify.constant.ActionType;
import dev.cheerfun.pixivic.biz.notify.mapper.NotifyMapper;
import dev.cheerfun.pixivic.biz.notify.po.NotifyRemind;
import dev.cheerfun.pixivic.biz.notify.po.NotifySettingConfig;
import dev.cheerfun.pixivic.biz.notify.sender.NotifySenderManager;
import dev.cheerfun.pixivic.biz.notify.util.UserSettingUtil;
import dev.cheerfun.pixivic.biz.web.collection.po.Collection;
import dev.cheerfun.pixivic.biz.web.collection.service.CollectionService;
import dev.cheerfun.pixivic.biz.web.comment.po.Comment;
import dev.cheerfun.pixivic.biz.web.comment.service.CommentService;
import dev.cheerfun.pixivic.biz.web.common.po.User;
import dev.cheerfun.pixivic.biz.web.illust.service.IllustrationBizService;
import dev.cheerfun.pixivic.biz.web.user.service.CommonService;
import dev.cheerfun.pixivic.common.po.Illustration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public abstract class NotifyEventCustomer {
    @Autowired
    protected NotifyMapper notifyMapper;
    @Autowired
    protected CommonService userCommonService;
    @Autowired
    protected CollectionService collectionService;
    @Autowired
    protected IllustrationBizService illustrationBizService;
    @Autowired
    protected CommentService commentService;
    @Autowired
    protected NotifySenderManager notifySenderManager;
    @Autowired
    protected UserSettingUtil userSettingUtil;
    protected Map<String, NotifySettingConfig> notifySettingMap;
    @Autowired
    protected StringRedisTemplate stringRedisTemplate;
    protected Map<String, Integer> remindTypeMap;

    @PostConstruct
    void init() {
        remindTypeMap = new HashMap<>();
        remindTypeMap.put(ActionType.COMMENT, 1);
        remindTypeMap.put(ActionType.REPLY, 1);
        remindTypeMap.put(ActionType.LIKE, 2);
        remindTypeMap.put(ActionType.BOOKMARK, 3);
        notifySettingMap = notifyMapper.queryNotifySettingConfig().stream().collect(Collectors.toMap(e -> e.getObjectType() + ":" + e.getAction(), e -> e));
    }

    protected void process(Event event) {
        //查找出所有收件方
        try {
            Object sendTo = querySendTo(event);
            //生成remind
            List<NotifyRemind> notifyReminds = new ArrayList<>();
            if (sendTo instanceof List) {
                for (Integer userId : (List<Integer>) sendTo) {
                    //校验是否接收消息
                    if (userSettingUtil.checkUserSetting(userId, event.getObjectType())) {
                        notifyReminds.add(generateRemind(event, userId));
                    }
                }
                //发送
                for (NotifyRemind notifyRemind : notifyReminds) {
                    send(notifyRemind);
                }
            } else {
                send(generateRemind(event, (int) sendTo));
            }
        } catch (Exception e) {
            System.err.println("消息消费出错，详情:");
            e.printStackTrace();
        }

    }

    protected abstract void consume(Event event);

    protected abstract Object querySendTo(Event event);

    protected abstract NotifyRemind generateRemind(Event event, Integer sendTo);

    protected abstract Boolean send(NotifyRemind notifyRemind);

    protected String queryTemplate(String objectType, String objectAction) {
        NotifySettingConfig notifySettingConfig = notifySettingMap.get(objectType + ":" + objectAction);
        return notifySettingConfig.getMessageTemplate();
    }

    protected String generateTitle(String appType, Integer appId) {
        return StringUtils.abbreviate(queryTitleByAppTypeAndAppId(appType, appId), 25);
    }

    protected String queryTitleByAppTypeAndAppId(String appType, Integer appId) {
        switch (appType) {
            case ObjectType.ILLUST:
                Illustration illustration = illustrationBizService.queryIllustrationById(appId);
                return illustration.getTitle();
            case ObjectType.COLLECTION:
                Collection collection = collectionService.queryCollectionByIdFromDb(appId);
                return collection.getTitle();
            case ObjectType.COMMENT:
                Comment comment = commentService.queryCommentById(appId);
                return comment.getContent();
            default:
                return "";
        }

    }

    protected User queryUserByAppTypeAndAppId(String appType, Integer appId) {
        return userCommonService.queryUser(queryUserIdByAppTypeAndAppId(appType, appId));
    }

    protected Integer queryUserIdByAppTypeAndAppId(String appType, Integer appId) {
        switch (appType) {
            case ObjectType.COLLECTION:
                Collection collection = collectionService.queryCollectionByIdFromDb(appId);
                return collection.getUserId();
            case ObjectType.COMMENT:
                Comment comment = commentService.queryCommentById(appId);
                return comment.getReplyFrom();
            default:
                return 0;
        }

    }

}
