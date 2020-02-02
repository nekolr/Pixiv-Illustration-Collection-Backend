package dev.cheerfun.pixivic.biz.web.rank.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cheerfun.pixivic.basic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.basic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.biz.web.rank.po.Rank;
import dev.cheerfun.pixivic.biz.web.rank.service.RankService;
import dev.cheerfun.pixivic.biz.web.user.service.BusinessService;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/12 16:16
 * @description RankController
 */
@RestController
@RequestMapping("/ranks")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RankController {
    private final RankService rankService;
    private final BusinessService businessService;
    private final ObjectMapper objectMapper;

    @GetMapping
    @PermissionRequired(PermissionLevel.ANONYMOUS)
    public ResponseEntity<Result<Rank>> queryByDateAndMode(@RequestParam String date, @RequestParam String mode, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "30") int pageSize, @RequestHeader(value = "Authorization", required = false) String token) {
        Rank rank = rankService.queryByDateAndMode(date, mode, page, pageSize);
        //由于jackson反序列化如果使用泛型则会将对象反序列化为linkedhashmap,这里重新序列化做一个转换,会降低效率
        List<Illustration> illustrationList = objectMapper.convertValue(rank.getData(), new TypeReference<List<Illustration>>() {
        });
        businessService.dealIsLikedInfoForIllustList(illustrationList);
        rank.setData(illustrationList);
        return ResponseEntity.ok().body(new Result<>("获取排行成功", rank));
    }

}
