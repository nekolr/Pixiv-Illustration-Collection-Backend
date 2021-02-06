package dev.cheerfun.pixivic.biz.web.vip.service;

import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.biz.web.user.service.CommonService;
import dev.cheerfun.pixivic.biz.web.vip.constant.ExchangeCodeBizType;
import dev.cheerfun.pixivic.biz.web.vip.constant.ExchangeCodeType;
import dev.cheerfun.pixivic.biz.web.vip.mapper.VIPMapper;
import dev.cheerfun.pixivic.biz.web.vip.po.ExchangeCode;
import dev.cheerfun.pixivic.biz.web.vip.util.ExchangeCodeUtil;
import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/13 7:39 PM
 * @description VIPUserService
 */
@Service
public class VIPUserService {
    private CommonService commonService;
    private VIPMapper vipMapper;

    public void setCommonService(CommonService commonService) {
        this.commonService = commonService;
    }

    public void setVipMapper(VIPMapper vipMapper) {
        this.vipMapper = vipMapper;
    }

    @Transactional
    public void exchangeCodeToDb(Integer userId, ExchangeCode exchangeCode) {
        //更新兑换码是否使用
        if (vipMapper.updateExchangeCode(exchangeCode.getId(), userId) == 1) {
            // 如果type是0则是身份验证兑换码 -1则是邀请码
            if (exchangeCode.getType() == ExchangeCodeType.INVITE || exchangeCode.getType() == ExchangeCodeType.VERIFY) {
                //
                return;
            }
            //更新用户会员状态
            commonService.updateUserPermissionLevel(userId, exchangeCode.getType());
        }
    }

    //每天晚上四点定时刷新用户角色
    @Scheduled(cron = "0 0 2 * * ?")
    @CacheEvict(value = "users", allEntries = true)
    public void refreshUserPermissionLevel() {
        //更新已经过期的会员
        vipMapper.refreshUserPermissionLevel();
    }

    //判断是否试用过
    //增加一个会员试用表
    @Cacheable(value = "CanParticipateActivity", key = "#userId+'-'+#activityName")
    public Boolean checkCanParticipateActivity(Integer userId, String activityName) {
        //用户id对100取余大于当前日期-2020-12-21的天数
        if (userId % 100 < 8 * Period.between(LocalDate.of(2020, 12, 21), LocalDate.now()).getDays()) {
            return vipMapper.checkActivity(userId, activityName) == null;
        }
        return false;
    }

    @CacheEvict(value = "CanParticipateActivity", key = "#userId+'-'+#activityName")
    public void addParticipateActivityLog(Integer userId, String activityName) {
        vipMapper.addParticipateActivityLog(userId, activityName);
    }

    //试用会员活动
    @Transactional
    public Boolean participateActivity(Integer userId, String activityName) {
        //活动名称校验
        //由于活动时效性 这里就采用硬编码
        if ("try".equals(activityName) && checkCanParticipateActivity(userId, activityName)) {
            commonService.updateUserPermissionLevel(userId, ExchangeCodeType.ONE_DAY_VIP);
            addParticipateActivityLog(userId, activityName);
            return true;
        } else {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "已经参与过活动或活动已经失效");
        }
    }

}
