package dev.cheerfun.pixivic.auth.util;


import dev.cheerfun.pixivic.auth.config.AuthProperties;
import dev.cheerfun.pixivic.common.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtil implements Serializable {

    @Autowired
    private AuthProperties authProperties;

    private Claims getAllClaimsFromToken(String token) {
        JwtParser jwtParser = Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(authProperties.getSecret().getBytes()));
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public String getToken(User user) {
        return generateToken(user, 0);
    }

    private String refreshToken(User user, int refreshCount) {
        return generateToken(user, refreshCount < 3 ? ++refreshCount : refreshCount);
    }

    private String generateToken(User user, int refreshCount) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("level", user.getLevel());
        claims.put("uid", user.getUid());
        claims.put("refreshCount", refreshCount);
        long expirationTimeLong = Long.parseLong(authProperties.getExpirationTime());
        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + (refreshCount + 1) * expirationTimeLong * 1000);
        return Jwts.builder()
                .setIssuer("pixivic.com")
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(authProperties.getSecret().getBytes()))
                .compact();
    }

    public User validateToken(String token) throws Exception {
        //成功则返回user 失败抛出未授权异常，但是如果要刷新token，我想也在这里完成，因为如果后面判断token是否过期
        // ，就还需要再解析一次token，解token是比较消耗性能的，因此这里需要一个东西存token
        //超时时间可以随着刷新自增长 最大为7天
        Claims claims = getAllClaimsFromToken(token);
        long difference = claims.getExpiration().getTime() - System.currentTimeMillis();
        if (difference < 0) {
            //无效 抛token过期异常
            throw new Exception();
        }
        User user = getUser(claims);
        if (difference < authProperties.getRefreshInterval()) {
            //小于一定区间，刷新
            refreshToken(user, claims.get("refreshCount", Integer.class));
        }
        return user;
    }

    private User getUser(Claims claims) {
        User user = new User();
        user.setUid(claims.get("uid", Integer.class));
        user.setLevel(claims.get("level", Integer.class));
        user.setUsername(claims.getSubject());
        return user;
    }

}
