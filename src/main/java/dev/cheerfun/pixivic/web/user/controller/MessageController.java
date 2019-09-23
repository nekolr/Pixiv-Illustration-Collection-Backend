package dev.cheerfun.pixivic.web.user.controller;

import dev.cheerfun.pixivic.auth.annotation.PermissionRequired;
import dev.cheerfun.pixivic.auth.constant.PermissionLevel;
import dev.cheerfun.pixivic.common.model.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2019/09/14 13:01
 * @description MessageController
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/users/messages")
public class MessageController {
    @PostMapping("/admin")
    @PermissionRequired(PermissionLevel.ADMIN)
    public ResponseEntity<Result<String>> sendToAll( @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("",""));
    }
    @PostMapping
    @PermissionRequired
    public ResponseEntity<Result<String>> sendToOther( @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("",""));
    }

    @GetMapping
    @PermissionRequired
    public ResponseEntity<Result<String>> query(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(new Result<>("",""));
    }


}
