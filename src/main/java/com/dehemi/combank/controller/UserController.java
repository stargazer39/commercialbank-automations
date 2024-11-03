package com.dehemi.combank.controller;

import com.dehemi.combank.JwtUtil;
import com.dehemi.combank.config.UsersConfig;
import com.dehemi.combank.dao.IntrospectRequest;
import com.dehemi.combank.dao.Token;
import com.dehemi.combank.dao.TokenRequest;
import com.dehemi.combank.dao.User;
import com.dehemi.combank.exceptions.TokenInvalidException;
import com.dehemi.combank.exceptions.UsernamePasswordWrongException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("user")
@Slf4j
//@AllArgsConstructor
public class UserController {
    final UsersConfig usersConfig;
    final JwtUtil jwtUtil;

    public UserController(UsersConfig usersConfig, JwtUtil jwtUtil) {
        this.usersConfig = usersConfig;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(path = "token",consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<Token> token(@RequestBody TokenRequest tokenRequest) {
        String username = tokenRequest.getUsername();
        String password = tokenRequest.getPassword();

        User user = usersConfig.getUsers().get(username);

        if(user == null || !password.equals(user.getPassword())) {
            return Mono.error(new UsernamePasswordWrongException());
        }

        Token.TokenBuilder builder = Token.builder();
        builder.accessToken(jwtUtil.createToken(user));

        return Mono.just(builder.build());
    }

    @PostMapping(path = "introspect",consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<String> getTokenValidity(@RequestBody IntrospectRequest tokenRequest) {
        String user = jwtUtil.getAssociatedUser(tokenRequest.getToken());

        if(user == null) {
            return Mono.error(new TokenInvalidException());
        }

        return Mono.just(user);
    }
}
