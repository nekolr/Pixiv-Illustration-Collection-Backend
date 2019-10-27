package dev.cheerfun.pixivic.web.news.controller;

import dev.cheerfun.pixivic.common.model.ACGNew;
import dev.cheerfun.pixivic.common.model.Result;
import dev.cheerfun.pixivic.web.news.service.NewsBIZService;
import dev.cheerfun.pixivic.web.rank.model.Rank;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/10/20 上午11:41
 * @description NewsBIZController
 */
@RestController
@RequestMapping("/news")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NewsBIZController {
    private final NewsBIZService newsBIZService;

    @GetMapping("/{referer}")
    public ResponseEntity<Result< List<ACGNew>>> queryByDateAndMode(@PathVariable String referer, @RequestParam String date, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "30") int pageSize) {

        List<ACGNew> acgNewList = newsBIZService.queryByFromAndDate(referer, date, page, pageSize);
        return ResponseEntity.ok().body(new Result<>("获取"+referer+"资讯列表成功", acgNewList));
    }
}
