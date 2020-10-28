package dev.cheerfun.pixivic.biz.web.admin.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.biz.proxy.po.VIPProxyServer;
import dev.cheerfun.pixivic.biz.proxy.service.VIPProxyServerService;
import dev.cheerfun.pixivic.biz.web.admin.service.AdminService;
import dev.cheerfun.pixivic.biz.web.vip.service.VIPUserService;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/28 5:17 PM
 * @description VIPAdminController
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RequestMapping("/admin")
public class VIPAdminController {
    private final VIPUserService vipUserService;
    private final VIPProxyServerService vipProxyServerService;

    //生成兑换码
    @GetMapping("/exchangeCode")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<List<String>>> getCheckEmail(@RequestParam byte type, @RequestParam Integer sum, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("生成兑换码成功", vipUserService.generateExchangeCode(type, sum)));
    }

    //添加高速服务器
    @PostMapping("/vipProxyServer")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result> addVipProxyServer(@RequestParam String url, @RequestHeader("Authorization") String token) {
        vipProxyServerService.addServer(url);
        return ResponseEntity.ok().body(new Result<>("添加高速服务器成功"));
    }

    //获取高速服务器
    @GetMapping("/vipProxyServer")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<List<VIPProxyServer>>> queryVipProxyServer(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("获取高速服务器成功", vipProxyServerService.queryAllServer()));
    }

}
