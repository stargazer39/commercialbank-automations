package com.dehemi.combank.filters;

import com.dehemi.combank.JwtUtil;
import com.dehemi.combank.config.PublicEndpoints;
import com.dehemi.combank.config.UsersConfig;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.remote.http.HttpMethod;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
public class AuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private UsersConfig usersConfig;
    private final PublicEndpoints publicEndpoints;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)
            throws ServletException {
        String path = request.getRequestURI();
        if(request.getMethod().equals("OPTIONS")){
            return true;
        }
        boolean filtered = publicEndpoints.getEndpoints().contains(path);
        return filtered;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if(authHeader == null || authHeader.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String[] headerParts = authHeader.split(" ");

        if(headerParts.length != 2) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String username = jwtUtil.getAssociatedUser(headerParts[1]);

        if(username == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        request.setAttribute("user", usersConfig.getUsers().get(username));

        filterChain.doFilter(request, response);
    }
}
