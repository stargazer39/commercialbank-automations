package com.dehemi.combank.websocket;

import com.dehemi.combank.JwtUtil;
import com.dehemi.combank.dao.TransactionScanLog;
import com.dehemi.combank.exceptions.TokenInvalidException;
import com.dehemi.combank.services.EventManagerService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class ReactiveWebSocketHandler implements WebSocketHandler {
    EventManagerService eventManagerService;
    private final JwtUtil jwtUtil;

    public ReactiveWebSocketHandler(EventManagerService eventManagerService, JwtUtil jwtUtil) {
        this.eventManagerService = eventManagerService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        String token = UriComponentsBuilder.fromUri(webSocketSession.getHandshakeInfo().getUri()).build().getQueryParams().getFirst("token");
        String username = jwtUtil.getAssociatedUser(token);

        if(username == null) {
            return Mono.error(new TokenInvalidException());
        }

        return webSocketSession.send(eventManagerService.getTransactionScanLogFlux(username).map(this::transactionLogToString).map(webSocketSession::textMessage));
    }

    private String transactionLogToString(TransactionScanLog transactionScanLog) {
        return transactionScanLog.toString();
    }
}
