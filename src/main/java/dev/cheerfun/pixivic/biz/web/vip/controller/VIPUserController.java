package dev.cheerfun.pixivic.biz.web.vip.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.util.JWTUtil;
import dev.cheerfun.pixivic.biz.proxy.po.VIPProxyServer;
import dev.cheerfun.pixivic.biz.proxy.service.VIPProxyServerService;
import dev.cheerfun.pixivic.biz.web.common.po.User;
import dev.cheerfun.pixivic.biz.web.user.service.CommonService;
import dev.cheerfun.pixivic.biz.web.vip.service.VIPUserService;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/13 7:39 PM
 * @description VIPUserController
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VIPUserController {
    private final VIPProxyServerService vipProxyServerService;
    private final VIPUserService vipUserService;
    private final CommonService userService;
    private final JWTUtil jwtUtil;

    //获取高速服务器
    @GetMapping("/vipProxyServer")
    @PermissionRequired
    public ResponseEntity<Result<List<VIPProxyServer>>> queryVipProxyServer(@RequestHeader(AuthConstant.AUTHORIZATION) String token) {
        Integer uid = (Integer) AppContext.get().get(AuthConstant.USER_ID);
        return ResponseEntity.ok().header(AuthConstant.AUTHORIZATION, jwtUtil.getToken(userService.queryUser(uid))).body(new Result<>("获取高速服务器成功", vipProxyServerService.queryAllServer()));
    }

    //查看活动送会员是否参与过
    @GetMapping("/vipActivity/{activityName}/canParticipateStatus")
    @PermissionRequired
    public ResponseEntity<Result<Boolean>> checkCanParticipateActivity(@PathVariable String activityName, @RequestHeader(AuthConstant.AUTHORIZATION) String token) {
        return ResponseEntity.ok().body(new Result<>("获取活动可参与状态成功", vipUserService.checkCanParticipateActivity((int) AppContext.get().get(AuthConstant.USER_ID), activityName)));
    }

    @PutMapping("/vipActivity/{activityName}/participateStatus")
    @PermissionRequired
    public ResponseEntity<Result<User>> participateActivity(@PathVariable String activityName, @RequestHeader(AuthConstant.AUTHORIZATION) String token) {
        Integer userId = (int) AppContext.get().get(AuthConstant.USER_ID);
        vipUserService.participateActivity(userId, activityName);
        User user = userService.queryUser(userId);
        return ResponseEntity.ok().header(AuthConstant.AUTHORIZATION, jwtUtil.getToken(user)).body(new Result<>("参与活动成功", user));
    }

}
