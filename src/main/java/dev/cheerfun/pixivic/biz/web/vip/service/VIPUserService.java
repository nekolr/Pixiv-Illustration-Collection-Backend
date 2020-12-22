package dev.cheerfun.pixivic.biz.web.vip.service;

import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.biz.web.user.service.CommonService;
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

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/13 7:39 PM
 * @description VIPUserService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VIPUserService {
    private final ExchangeCodeUtil exchangeCodeUtil;
    private final StringRedisTemplate stringRedisTemplate;
    private final CommonService commonService;
    private final VIPMapper VIPMapper;

    //按照类型和数量生成兑换码
    public List<String> generateExchangeCode(byte type, Integer sum) {
        //for循环
        String now = LocalDate.now().toString();
        List<String> result = new ArrayList<>(sum);
        for (int i = 0; i < sum; i++) {
            //获取当前可用的序列号+1
            try {
                int sn = stringRedisTemplate.opsForValue().increment(RedisKeyConstant.VIP_CODE_SERIAL_NUMBER).intValue();
                //生成兑换码
                String exchangeCode = exchangeCodeUtil.generateExchangeCode(sn, type);
                //存入数据库
                VIPMapper.inertExchangeCode(sn, type, now);
                result.add(exchangeCode);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return result;
    }

    //使用验证码
    @Transactional
    public Boolean exchangeVIP(Integer userId, String exchangeStringCode) {
        if (exchangeStringCode.length() != 16) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "兑换码无效");
        }
        //校验转化
        ExchangeCode exchangeCode = exchangeCodeUtil.validateExchangeCode(exchangeStringCode);
        if (exchangeCode == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "兑换码无效");
        }
        //校验兑换码是否使用
        Boolean useFlag = stringRedisTemplate.opsForValue().getBit(RedisKeyConstant.VIP_CODE_USAGE_RECORD_BITMAP, exchangeCode.getId());
        if (useFlag) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "兑换码已经被使用");
        }
        //更新用户会员状态
        commonService.updateUserPermissionLevel(userId, exchangeCode.getType());
        //更新兑换码是否使用
        VIPMapper.updateExchangeCode(exchangeCode.getId());
        stringRedisTemplate.opsForValue().setBit(RedisKeyConstant.VIP_CODE_USAGE_RECORD_BITMAP, exchangeCode.getId(), true);
        //返回
        return true;
    }
    //根据编号查询验证码

    //每天晚上四点定时刷新用户角色
    @Scheduled(cron = "0 0 2 * * ?")
    public void refreshUserPermissionLevel() {
        //更新已经过期的会员
        commonService.refreshUserPermissionLevel();
    }

    //判断是否试用过
    //增加一个会员试用表
    @Cacheable(value = "CanParticipateActivity", key = "#userId+'-'+#activityName")
    public Boolean checkCanParticipateActivity(Integer userId, String activityName) {
        //用户id对100取余 定义一个变量 为当前日期-2020-12-22的天数
        if (userId % 100 < 2 * Period.between(LocalDate.of(2020, 12, 22), LocalDate.now().plusDays(1)).getDays()) {
            return VIPMapper.checkActivity(userId, activityName) == null;
        }
        return false;
    }

    @CacheEvict(value = "CanParticipateActivity", key = "#userId+'-'+#activityName")
    public void addParticipateActivityLog(Integer userId, String activityName) {
        VIPMapper.addParticipateActivityLog(userId, activityName);
    }

    //试用会员活动
    @Transactional
    public Boolean participateActivity(Integer userId, String activityName) {
        //活动名称校验
        //由于活动时效性 这里就采用硬编码
        if ("try".equals(activityName) && checkCanParticipateActivity(userId, activityName)) {
            commonService.updateUserPermissionLevel(userId, ExchangeCodeType.ONE_DAY);
            addParticipateActivityLog(userId, activityName);
            return true;
        } else {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "已经参与过活动或活动已经失效");
        }
    }

}
