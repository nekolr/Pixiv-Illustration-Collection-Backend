package dev.cheerfun.pixivic.biz.web.vip.service;

import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.biz.web.user.service.CommonService;
import dev.cheerfun.pixivic.biz.web.vip.constant.ExchangeCodeType;
import dev.cheerfun.pixivic.biz.web.vip.mapper.VIPMapper;
import dev.cheerfun.pixivic.biz.web.vip.po.ExchangeCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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

    @Autowired
    public void setCommonService(CommonService commonService) {
        this.commonService = commonService;
    }

    @Autowired
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
    @Cacheable(value = "CanParticipateActivity", key = "#userId+'-'+#activityName")
    public Boolean checkCanParticipateActivity(Integer userId, String activityName) {
        return vipMapper.checkActivity(userId, activityName) == null;
    }

    //判断是否试用过
    @Cacheable(value = "CanParticipateWXActivity", key = "#openid+'-'+#activityName")
    public Boolean checkCanParticipateWXActivity(String openid, String activityName) {
        return vipMapper.checkWXActivity(openid, activityName) == null;
    }

    @CacheEvict(value = "CanParticipateActivity", key = "#userId+'-'+#activityName")
    public void addParticipateActivityLog(Integer userId, String activityName) {
        vipMapper.addParticipateActivityLog(userId, activityName);
    }

    @CacheEvict(value = "CanParticipateWXActivity", key = "#openid+'-'+#activityName")
    public void addParticipateWXActivityLog(String openid, String activityName) {
        vipMapper.addParticipateWXActivityLog(openid, activityName);
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
