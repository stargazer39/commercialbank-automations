package com.dehemi.combank.services;

import com.dehemi.combank.dao.TransactionScanLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class EventManagerService {
    private final SimpMessageSendingOperations messagingTemplate;

    public EventManagerService(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendTransactionScanLogEvent(TransactionScanLog transactionScanLog) {
        messagingTemplate.convertAndSend("/topic/transactions", transactionScanLog);
    }

    public TransactionScanLog getTransactionScanLogFlux(String userId) {
        return null;
    }
}
