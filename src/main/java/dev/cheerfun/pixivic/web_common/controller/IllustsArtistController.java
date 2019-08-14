package dev.cheerfun.pixivic.web_common.controller;


import dev.cheerfun.pixivic.ratelimit.annotation.RateLimit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 画师简单信息 前端控制器
 * </p>
 *
 * @author huangsm
 * @since 2019-06-28
 */
@RestController
@RequestMapping("/web_common/artist")

public class IllustsArtistController {

    @GetMapping("/hello")
    @RateLimit(limitNum = 10)
    public String hello() {
        return "hello";
    }

}

