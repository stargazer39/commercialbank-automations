package com.dehemi.combank.websocket;

import com.dehemi.combank.dao.TransactionScanLog;
import com.dehemi.combank.services.EventManagerService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
public class ReactiveWebSocketHandler implements WebSocketHandler {
    EventManagerService eventManagerService;

    public ReactiveWebSocketHandler(EventManagerService eventManagerService) {
        this.eventManagerService = eventManagerService;
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        return webSocketSession.send(eventManagerService.getTransactionScanLogFlux().map(this::transactionLogToString).map(webSocketSession::textMessage).log());
    }

    private String transactionLogToString(TransactionScanLog transactionScanLog) {
        return transactionScanLog.toString();
    }
}
