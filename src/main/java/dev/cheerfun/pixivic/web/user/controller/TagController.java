package dev.cheerfun.pixivic.web.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/14 12:56
 * @description TagController
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/illustrations/tags")
public class TagController {
    //自动完成
}
