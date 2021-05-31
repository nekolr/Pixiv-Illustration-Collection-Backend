package dev.cheerfun.pixivic.biz.web.admin.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.biz.dns.service.JDDNSModifyService;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/6/1 7:55 AM
 * @description JDDNSController
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RequestMapping("/admin")
public class JDDNSController {
    private final JDDNSModifyService jddnsModifyService;

    @PutMapping("/dnsLB")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> modifyDns(
            @RequestParam(defaultValue = "1") Integer weight,
            @RequestHeader(value = AuthConstant.AUTHORIZATION) String token) {
        jddnsModifyService.modifyDNSLB(weight);
        return ResponseEntity.ok().body(new Result<>("修改负载状态成功", true));
    }

}
