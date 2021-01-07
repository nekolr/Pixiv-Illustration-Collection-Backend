package dev.cheerfun.pixivic.biz.web.user.controller;

import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.biz.credit.po.CreditHistory;
import dev.cheerfun.pixivic.biz.web.user.service.UserCreditService;
import dev.cheerfun.pixivic.common.constant.AuthConstant;
import dev.cheerfun.pixivic.common.context.AppContext;
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
 * @date 2020/10/30 7:12 PM
 * @description UserCreditController
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@PermissionRequired
@RequestMapping("/users")
public class UserCreditController {
    private final UserCreditService userCreditService;

    //获取积分历史
    @GetMapping("/{userId}/creditHistory")
    @PermissionRequired
    public ResponseEntity<Result<List<CreditHistory>>> queryCreditHistoryList(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "30") @Max(30) Integer pageSize, @RequestHeader(AuthConstant.AUTHORIZATION) String token) {
        int userId = (int) AppContext.get().get(AuthConstant.USER_ID);
        return ResponseEntity.ok().body(new Result<>("查询近期积分历史成功", userCreditService.queryCreditHistoryCount(userId), userCreditService.queryCreditHistoryList(userId, page, pageSize)));
    }
}
