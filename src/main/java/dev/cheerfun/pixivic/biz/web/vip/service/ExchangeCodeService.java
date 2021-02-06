package dev.cheerfun.pixivic.biz.web.vip.service;

import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.biz.web.vip.constant.ExchangeCodeBizType;
import dev.cheerfun.pixivic.biz.web.vip.constant.ExchangeCodeType;
import dev.cheerfun.pixivic.biz.web.vip.mapper.VIPMapper;
import dev.cheerfun.pixivic.biz.web.vip.po.ExchangeCode;
import dev.cheerfun.pixivic.biz.web.vip.util.ExchangeCodeUtil;
import dev.cheerfun.pixivic.common.constant.RedisKeyConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/2/6 7:32 PM
 * @description ExchangeCodeService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ExchangeCodeService {
    private final ExchangeCodeUtil exchangeCodeUtil;
    private final VIPMapper vipMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final VIPUserService vipUserService;
    private Set<Byte> VIPExchangeCodeTypeSet;

    @PostConstruct
    public void init() {
        VIPExchangeCodeTypeSet = new HashSet<>();
        VIPExchangeCodeTypeSet.add(ExchangeCodeType.ONE_DAY_VIP);
        VIPExchangeCodeTypeSet.add(ExchangeCodeType.THREE_DAYS_VIP);
        VIPExchangeCodeTypeSet.add(ExchangeCodeType.SEVEN_DAYS_VIP);
        VIPExchangeCodeTypeSet.add(ExchangeCodeType.FIFTEEN_DAYS_VIP);
        VIPExchangeCodeTypeSet.add(ExchangeCodeType.THIRTY_DAYS_VIP);
    }

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
                vipMapper.inertExchangeCode(sn, type, now);
                result.add(exchangeCode);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return result;
    }

    //使用验证码

    public Boolean exchangeCode(Integer userId, String exchangeStringCode, String exchangeCodeBizType) {
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
        if (!checkBizType(exchangeCode.getType(), exchangeCodeBizType)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "兑换码类型不正确");
        }
        vipUserService.exchangeCode(userId, exchangeCode);
        stringRedisTemplate.opsForValue().setBit(RedisKeyConstant.VIP_CODE_USAGE_RECORD_BITMAP, exchangeCode.getId(), true);
        //返回
        return true;
    }

    private boolean checkBizType(byte type, String exchangeCodeBizType) {
        switch (exchangeCodeBizType) {
            case ExchangeCodeBizType
                    .INVITE:
                return ExchangeCodeType.INVITE == type;
            case ExchangeCodeBizType
                    .VERIFY:
                return ExchangeCodeType.VERIFY == type;
            case ExchangeCodeBizType
                    .VIP:
                return VIPExchangeCodeTypeSet.contains(type);
            default:
                return false;
        }

    }
}
