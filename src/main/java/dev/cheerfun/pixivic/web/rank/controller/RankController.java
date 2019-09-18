package dev.cheerfun.pixivic.web.rank.controller;

import dev.cheerfun.pixivic.common.model.Result;
import dev.cheerfun.pixivic.web.rank.model.Rank;
import dev.cheerfun.pixivic.web.rank.service.RankService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<Result<String>> insert(@RequestBody Rank rank) {
        boolean result = rankService.insert(rank);
        String message;
        HttpStatus httpStatus;
        if (result) {
            message = "插入成功";
            httpStatus = HttpStatus.OK;
        } else {
            message = "插入失败";
            httpStatus = HttpStatus.EXPECTATION_FAILED;
        }
        return ResponseEntity.status(httpStatus).body(new Result<>(message));
    }

    @GetMapping
    public ResponseEntity<Result<Rank>> queryByDateAndMode(@RequestParam String date, @RequestParam String mode,int page, @RequestParam(defaultValue = "30") int pageSize) {
        Rank rank = rankService.queryByDateAndMode(date, mode, page, pageSize);
        return ResponseEntity.ok().body(new Result<>("获取日排行成功", rank));
    }

}
