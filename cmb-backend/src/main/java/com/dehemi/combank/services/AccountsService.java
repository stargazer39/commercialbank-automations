package com.dehemi.combank.services;

import com.dehemi.combank.dao.Account;
import com.dehemi.combank.dao.Transaction;
import com.dehemi.combank.dao.TransactionScanLog;
import com.dehemi.combank.exceptions.CSVProcessException;
import com.dehemi.combank.repo.TransactionRepository;
import com.dehemi.combank.repo.TransactionsScanLogRepository;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class AccountsService {
    final TransactionRepository transactionRepository;
    final TransactionsScanLogRepository transactionsScanLogRepository;
    final EventManagerService eventManagerService;

    public AccountsService(TransactionRepository transactionRepository,TransactionsScanLogRepository transactionsScanLogRepository, EventManagerService eventManagerService) {
        this.transactionRepository = transactionRepository;
        this.transactionsScanLogRepository = transactionsScanLogRepository;
        this.eventManagerService = eventManagerService;
    }

    @Transactional
    public void processAccount(Account a, CombankInstance instance) throws CSVProcessException, CsvValidationException, IOException, InterruptedException {
        log.info("account number - {} type - {} available total - {} current balance - {}", a.getAccountNumber(), a.getAccountType(),a.getAvailableTotal(), a.getCurrentTotal());
        List<Transaction> transactions = instance.getTransactions(a, false);

        List<Transaction> newTransaction = transactionRepository.insertAllNew(transactions.reversed());
        log.info("new transactions - {}", newTransaction.size());
        TransactionScanLog transactionScanLog = new TransactionScanLog();
        transactionScanLog.setNewLogs(newTransaction.size());
        transactionScanLog.setAccountNumber(a.getAccountNumber());
        transactionScanLog.setUserId(instance.getUser().getUsername());
        transactionScanLog.setNewTransactions(newTransaction);

        transactionsScanLogRepository.save(transactionScanLog);

        if(!newTransaction.isEmpty()) {
            eventManagerService.sendTransactionScanLogEvent(transactionScanLog);
        }
    }
}
