package dev.cheerfun.pixivic.biz.web.user.service;

import dev.cheerfun.pixivic.biz.web.common.exception.BusinessException;
import dev.cheerfun.pixivic.biz.web.common.po.User;
import dev.cheerfun.pixivic.biz.web.user.dto.VerifiedDTO;
import dev.cheerfun.pixivic.biz.web.user.dto.VerifiedResponseResult;
import dev.cheerfun.pixivic.biz.web.user.mapper.CommonMapper;
import dev.cheerfun.pixivic.biz.web.user.util.VerifiedUtil;
import dev.cheerfun.pixivic.biz.web.vip.service.VIPUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.Period;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/2/4 2:27 PM
 * @description VerifService
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VerifService {
    private final CommonService commonService;
    private final VIPUserService vipUserService;
    private final CommonMapper userMapper;
    private final VerifiedUtil verifiedUtil;

    //绑定身份信息
    //前端调用前需要提示用户确认信息是否准确 不准确也会消耗码
    public void verified(VerifiedDTO verifiedDTO, int userId) throws InterruptedException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        //校验是否绑定手机
        User user = commonService.queryUser(userId);
        if (user.getPhone() == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "请先绑定手机号");
        }
        if (user.getIdCard() != null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "已经实名过暂时不可更改");
        }
        //验证兑换码
        vipUserService.exchangeVIP(userId, verifiedDTO.getExchangeCode());
        //校验通过则调用实名验证api
        VerifiedResponseResult verifiedResponseResult = verifiedUtil.verifyUser(verifiedDTO.getName(), verifiedDTO.getIdCard(), user.getPhone());
        if (verifiedResponseResult == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "实名信息有误请重新购买兑换码后输入正确信息");
        }
        //调用后如果成功则更新addr和年龄以及生日信息
        updateUserVerifiedInfo(userId, verifiedResponseResult);
    }

    @CacheEvict(value = "users", key = "#userId")
    public void updateUserVerifiedInfo(int userId, VerifiedResponseResult verifiedResponseResult) {
        String birthdate = verifiedResponseResult.getBirthday();
        String birthYear = birthdate.substring(0, 4);
        String birthMonth = birthdate.substring(4, 6);
        String birthDay = birthdate.substring(6, 8);
        int age = Period.between(LocalDate.of(Integer.valueOf(birthYear), Integer.valueOf(birthMonth), Integer.valueOf(birthDay)), LocalDate.now()).getYears();
        userMapper.updateUserVerifiedInfo(userId, verifiedResponseResult.getIdcard(), birthYear + "-" + birthMonth + "-" + birthDay, age, verifiedResponseResult.getAddress());
    }
}
