package dev.cheerfun.pixivic.biz.web.spotlight.controller;

import dev.cheerfun.pixivic.biz.web.spotlight.service.SpotlightBizService;
import dev.cheerfun.pixivic.common.po.Illustration;
import dev.cheerfun.pixivic.common.po.Result;
import dev.cheerfun.pixivic.common.po.Spotlight;
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
@RequestMapping("/spotlights")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SpotlightBizController {
    private final SpotlightBizService spotlightBizService;

    @GetMapping
    public ResponseEntity<Result<List<Spotlight>>> query(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "15") int pageSize) {
        return ResponseEntity.ok().body(new Result<>("获取Spotlight列表成功",spotlightBizService.query(page,pageSize)));
    }

    @GetMapping("/{spotlightId}")
    public ResponseEntity<Result<Spotlight>> queryDetail(@PathVariable int spotlightId) {
        return ResponseEntity.ok().body(new Result<>("获取Spotlight详情成功",spotlightBizService.queryDetail(spotlightId)));
    }

    @GetMapping("/{spotlightId}/illustrations")
    public ResponseEntity<Result<List<Illustration>>> queryIllustrations(@PathVariable int spotlightId ) {
        return ResponseEntity.ok().body(new Result<>("获取该spotlight下画作列表成功",spotlightBizService.queryIllustrations(spotlightId)));
    }

}
