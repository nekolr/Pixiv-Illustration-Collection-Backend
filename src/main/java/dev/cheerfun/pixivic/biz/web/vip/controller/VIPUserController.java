package dev.cheerfun.pixivic.biz.web.vip.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.biz.proxy.po.VIPProxyServer;
import dev.cheerfun.pixivic.biz.proxy.service.VIPProxyServerService;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

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
    final private VIPProxyServerService vipProxyServerService;

    //获取高速服务器
    @GetMapping("/vipProxyServer")
    @PermissionRequired
    public ResponseEntity<Result<List<VIPProxyServer>>> queryVipProxyServer(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("获取高速服务器成功", vipProxyServerService.queryAllServer()));
    }

}
