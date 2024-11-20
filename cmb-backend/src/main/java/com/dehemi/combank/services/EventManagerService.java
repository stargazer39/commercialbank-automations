package com.dehemi.combank.services;

import com.dehemi.combank.clients.Gotify;
import com.dehemi.combank.dao.Transaction;
import com.dehemi.combank.dao.TransactionScanLog;
import com.dehemi.combank.dao.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class EventManagerService {
    private final SimpMessageSendingOperations messagingTemplate;
    private final Gotify gotify;

    public EventManagerService(SimpMessageSendingOperations messagingTemplate, Gotify gotify) {
        this.messagingTemplate = messagingTemplate;
        this.gotify = gotify;
    }

    public void sendTransactionScanLogEvent(TransactionScanLog transactionScanLog, User user) {
        messagingTemplate.convertAndSend("/topic/transactions", transactionScanLog);
        String message = "No transaction description";

        if(!transactionScanLog.getNewTransactions().isEmpty()) {
            Transaction transaction = transactionScanLog.getNewTransactions().get(0);
            message = String.format("%s - %s",transaction.getDebit() == null ? "Recieved" : "Paid", transaction.getDescription());
        }

        if(transactionScanLog.getNewTransactions().size() <= 1) {
            gotify.sendNotification("New transaction", message, 10, user.getGotifyToken());
        } else {
            gotify.sendNotification(String.format("New transactions +%d",transactionScanLog.getNewLogs()), message, 10, user.getGotifyToken());
        }
    }

    public TransactionScanLog getTransactionScanLogFlux(String userId) {
        return null;
    }
}
