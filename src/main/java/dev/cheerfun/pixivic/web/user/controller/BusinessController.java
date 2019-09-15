package dev.cheerfun.pixivic.web.user.controller;

import dev.cheerfun.pixivic.common.model.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/14 12:08
 * @description BizController
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/users")
public class BusinessController {
    @PostMapping("/bookmarked")
    public ResponseEntity<Result<String>> bookmark() {
        //redis中存一份联系
        //往mysql更新收藏数

        return ResponseEntity.ok().body(new Result<>("收藏成功", ""));
    }

    @PostMapping("/followed")
    public ResponseEntity<Result<String>> follow() {
        //redis中存一份联系
        return ResponseEntity.ok().body(new Result<>("收藏成功", ""));
    }

    @GetMapping("/actionHistory")
    public ResponseEntity<Result<String>> QueryActionHistory() {
        //查看行为历史
        return ResponseEntity.ok().body(new Result<>("收藏成功", ""));
    }

}
