package com.dehemi.combank.filters;

import com.dehemi.combank.JwtUtil;
import com.dehemi.combank.config.JWTConfig;
import com.dehemi.combank.config.UsersConfig;
import com.dehemi.combank.exceptions.TokenInvalidException;
import com.dehemi.combank.exceptions.UnknownAuthException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class AuthFilter implements WebFilter {
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final JwtUtil jwtUtil;
    private UsersConfig usersConfig;

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange,
                             WebFilterChain webFilterChain) {
        try {
            log.info("hit filter");
            String path = serverWebExchange.getRequest().getPath().pathWithinApplication().value();

            HttpMethod method = serverWebExchange.getRequest().getMethod();

            if(method.equals(HttpMethod.OPTIONS)) {
                return webFilterChain.filter(serverWebExchange);
            }

            if(pathMatcher.match("/user/token", path)) {
                return webFilterChain.filter(serverWebExchange);
            }

            if(pathMatcher.match("/user/introspect", path)) {
                return webFilterChain.filter(serverWebExchange);
            }

            String token;

            if(pathMatcher.match("/events/transactions",path)) {
                MultiValueMap<String, String> queryParams = serverWebExchange.getRequest().getQueryParams();
                if(!queryParams.containsKey("token")) {
                    return Mono.error(new TokenInvalidException());
                }
                token = queryParams.getFirst("token");

            } else {
                List<String> authHeader = serverWebExchange.getRequest().getHeaders().get("Authorization");

                if(authHeader == null || authHeader.isEmpty()) {
                    return Mono.error(new TokenInvalidException());
                }

                String[] headerParts = authHeader.getFirst().split(" ");

                if(headerParts.length != 2) {
                    return Mono.error(new TokenInvalidException());
                }

                token = headerParts[1];
            }

            String username = jwtUtil.getAssociatedUser(token);

            if(username == null) {
                return Mono.error(new TokenInvalidException());
            }

            serverWebExchange.getAttributes().put("user", usersConfig.getUsers().get(username));

            return webFilterChain.filter(serverWebExchange);
        }catch (Exception e) {
            return Mono.error(new UnknownAuthException(e));
        }
    }
}
