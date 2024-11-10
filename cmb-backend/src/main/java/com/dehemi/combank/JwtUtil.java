package com.dehemi.combank;

import com.dehemi.combank.config.JWTConfig;
import com.dehemi.combank.dao.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.IOException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@AllArgsConstructor
public class JwtUtil {
    final private JWTConfig jwtConfig;

    public Key getPrivateKey(){
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] secretKeyBytes = jwtConfig.getSecret().getBytes();

        return new SecretKeySpec(secretKeyBytes, signatureAlgorithm.getJcaName());
    }

    public String createToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getUsername()).build();
        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toMillis(1000000000));

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(tokenValidity)
                .signWith(getPrivateKey())
                .compact();
    }

    public String getAssociatedUser(String authToken) throws IOException {
            return Jwts.parser().setSigningKey(getPrivateKey()).build().parseClaimsJws(authToken).getPayload().getSubject();
    }
}
