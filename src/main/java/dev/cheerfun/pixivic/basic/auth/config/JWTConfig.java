package dev.cheerfun.pixivic.basic.auth.config;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2021/1/4 9:59 AM
 * @description JWTConfig
 */
@Configuration
public class JWTConfig {
    @Bean
    public JwtParser jwtParser(@Autowired SecretKey secretKey) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build();
    }

    @Bean
    public SecretKey secretKey(@Value("${jjwt.secret}") String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

}
