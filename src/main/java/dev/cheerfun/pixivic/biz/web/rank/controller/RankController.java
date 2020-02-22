package dev.cheerfun.pixivic.biz.web.rank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.basic.ad.annotation.WithAdvertisement;
import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.basic.userInfo.annotation.WithUserInfo;
import dev.cheerfun.pixivic.biz.web.rank.service.RankService;
import dev.cheerfun.pixivic.biz.web.user.service.BusinessService;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/12 16:16
 * @description RankController
 */
@RestController
@RequestMapping("/ranks")
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RankController {
    private final RankService rankService;

    @GetMapping
    @WithUserInfo
    @PermissionRequired(PermissionLevel.ANONYMOUS)
    public ResponseEntity<Result<List<Illustration>>> queryByDateAndMode(@RequestParam String date, @RequestParam String mode, @RequestParam(defaultValue = "1") @Max(30) int page, @RequestParam(defaultValue = "30") int pageSize, @RequestHeader(value = "Authorization", required = false) String token) {
        List<Illustration> rank = rankService.queryByDateAndMode(date, mode, page, pageSize);
        return ResponseEntity.ok().body(new Result<>("获取排行成功", rank));
    }

}
