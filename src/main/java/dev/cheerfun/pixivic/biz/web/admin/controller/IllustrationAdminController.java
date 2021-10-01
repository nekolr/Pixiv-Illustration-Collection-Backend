package dev.cheerfun.pixivic.biz.web.admin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.basic.ratelimit.annotation.RateLimit;
import dev.cheerfun.pixivic.biz.ad.annotation.WithAdvertisement;
import dev.cheerfun.pixivic.biz.userInfo.annotation.WithUserInfo;
import dev.cheerfun.pixivic.biz.web.admin.dto.IllustDTO;
import dev.cheerfun.pixivic.biz.web.admin.service.AdminService;
import dev.cheerfun.pixivic.biz.web.illust.service.RankService;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/10/28 5:09 PM
 * @description IllustrationAdminController
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RequestMapping("/admin")
public class IllustrationAdminController {
    private final AdminService adminService;
    private final RankService rankService;

    @GetMapping("/illusts/{illustId}")
    public ResponseEntity<Result<Illustration>> queryIllustrationById(@PathVariable Integer illustId, @RequestHeader(value = "Token") String token) throws JsonProcessingException {
        if (adminService.validateKey(token)) {
            log.info("管理员key:" + token + ",开始获取画作(" + illustId + ")详情");
            return ResponseEntity.ok().body(new Result<>("获取画作详情成功", adminService.queryIllustrationById(illustId)));
        }
        return ResponseEntity.ok().body(new Result<>("获取画作详情成功", null));
    }

    @GetMapping("/ranks")
    public ResponseEntity<Result<List<Illustration>>> queryByDateAndMode(@RequestParam String date, @RequestParam String mode, @RequestParam(defaultValue = "1") @Min(1) @Max(30) int page, @RequestParam(defaultValue = "30") int pageSize, @RequestHeader(value = AuthConstant.AUTHORIZATION, required = false) String token) {
        List<Illustration> rank = rankService.queryByDateAndMode(date, mode, page, pageSize);
        return ResponseEntity.ok().body(new Result<>("获取排行成功", rank));
    }

    @PutMapping("/illusts/{illustId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Illustration>> updateIllustrationById(@PathVariable Integer illustId, @RequestHeader(value = AuthConstant.AUTHORIZATION) String token, @RequestBody IllustDTO illustDTO) {
        adminService.updateIllusts(illustDTO);
        return ResponseEntity.ok().body(new Result<>("更新画作等级成功", null));
    }

    @PostMapping("/blockIllusts")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> blockIllustrationById(@RequestHeader(value = AuthConstant.AUTHORIZATION) String token, @RequestBody List<Integer> illustIdList) {
        adminService.blockIllustrationById(illustIdList);
        return ResponseEntity.ok().body(new Result<>("封禁画作成功", true));
    }

    @GetMapping("/blockIllusts")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<List<Integer>>> queryBlockIllust(@RequestParam(required = false) Integer illustId, @RequestHeader(value = AuthConstant.AUTHORIZATION) String token) {
        return ResponseEntity.ok().body(new Result<>("获取封禁画作成功", adminService.queryBlockIllust(illustId)));
    }

    @DeleteMapping("/blockIllusts/{illustId}")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<Boolean>> removeIllustFromBlockIllust(@PathVariable Integer illustId, @RequestHeader(value = AuthConstant.AUTHORIZATION) String token) {
        adminService.removeIllustFromBlockIllust(illustId);
        return ResponseEntity.ok().body(new Result<>("移除封禁画作成功", true));
    }
}
