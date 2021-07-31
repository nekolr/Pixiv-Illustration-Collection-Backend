package dev.cheerfun.pixivic.biz.web.wx.controller;

import dev.cheerfun.pixivic.biz.web.admin.service.AdminService;
import dev.cheerfun.pixivic.biz.web.vip.service.ExchangeCodeService;
import dev.cheerfun.pixivic.biz.web.vip.service.VIPUserService;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/7/31 12:00 AM
 * @description WXActivityController
 */
@Slf4j
@RestController
@AllArgsConstructor
public class WXActivityController {
    private final VIPUserService vipUserService;
    private final ExchangeCodeService exchangeCodeService;
    private final AdminService adminService;

    @GetMapping("/wx/activity/{activityName}")
    @Transactional
    public ResponseEntity<Result<String>> participateActivity(@PathVariable String activityName,
                                                              @RequestParam(name = "openid") String openid, @RequestParam(value = "token") String token) {
        if (adminService.validateKey(token)) {
            if (vipUserService.checkCanParticipateWXActivity(openid, activityName)) {
                List<String> code = exchangeCodeService.generateExchangeCode((byte) 30, 1);
                vipUserService.addParticipateWXActivityLog(openid, activityName);
                return ResponseEntity.ok().body(new Result<>("获取微信活动兑换码成功", code.get(0)));
            }
            return ResponseEntity.ok().body(new Result<>("获取微信活动兑换码失败", "已参与过该活动"));
        }
        return ResponseEntity.badRequest().body(new Result<>("", ""));
    }

}
