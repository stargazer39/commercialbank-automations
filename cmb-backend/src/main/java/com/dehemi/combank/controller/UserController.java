package com.dehemi.combank.controller;

import com.dehemi.combank.JwtUtil;
import com.dehemi.combank.config.UsersConfig;
import com.dehemi.combank.dao.IntrospectRequest;
import com.dehemi.combank.dao.Token;
import com.dehemi.combank.dao.TokenRequest;
import com.dehemi.combank.dao.User;
import com.dehemi.combank.exceptions.TokenInvalidException;
import com.dehemi.combank.exceptions.UsernamePasswordWrongException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Token token(@RequestBody TokenRequest tokenRequest) throws UsernamePasswordWrongException {
        String username = tokenRequest.getUsername();
        String password = tokenRequest.getPassword();

        User user = usersConfig.getUsers().get(username);

        if(user == null || !password.equals(user.getPassword())) {
            throw new UsernamePasswordWrongException();
        }

        return Token.builder()
                .accessToken(jwtUtil.createToken(user))
                .build();
    }

    @PostMapping(path = "introspect",consumes = {MediaType.APPLICATION_JSON_VALUE})
    public String getTokenValidity(@RequestBody IntrospectRequest tokenRequest) throws TokenInvalidException {
        String user = jwtUtil.getAssociatedUser(tokenRequest.getToken());

        if(user == null) {
            throw new TokenInvalidException();
        }

        return user;
    }
}
