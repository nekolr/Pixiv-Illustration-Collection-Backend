package dev.cheerfun.pixivic.web.controller;


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
@RequestMapping("/web/artist")
public class IllustsArtistController {
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

}

