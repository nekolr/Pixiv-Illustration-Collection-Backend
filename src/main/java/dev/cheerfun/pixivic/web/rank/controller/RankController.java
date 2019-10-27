package dev.cheerfun.pixivic.web.rank.controller;

import dev.cheerfun.pixivic.common.model.Result;
import dev.cheerfun.pixivic.web.rank.model.Rank;
import dev.cheerfun.pixivic.web.rank.service.RankService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping
    public ResponseEntity<Result<Rank>> queryByDateAndMode(@RequestParam String date, @RequestParam String mode, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "30") int pageSize) {
        Rank rank = rankService.queryByDateAndMode(date, mode, page, pageSize);
        return ResponseEntity.ok().body(new Result<>("获取排行成功", rank));
    }

}
