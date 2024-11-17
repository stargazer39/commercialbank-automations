package com.dehemi.combank.config;

import com.dehemi.combank.JwtUtil;
import com.dehemi.combank.dao.User;
import com.dehemi.combank.exceptions.TokenInvalidException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Objects;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;
    private final UsersConfig usersConfig;

    public WebSocketConfig(JwtUtil jwtUtil, UsersConfig usersConfig) {
        this.jwtUtil = jwtUtil;
        this.usersConfig = usersConfig;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/events");
//        registry.addEndpoint("/events").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // Access authentication header(s) and invoke accessor.setUser(user)
                    User user = usersConfig.getUsers().get(jwtUtil.getAssociatedUser(accessor.getPasscode()));
                    Objects.requireNonNull(accessor.getSessionAttributes()).put("user", user);
                    return message;
                } else {
                    if(Objects.requireNonNull(accessor.getSessionAttributes()).containsKey("user")){
                        return message;
                    }
                }
                throw new RuntimeException("invalid access token for websocket message");
            }
        });
    }

}
