package com.dehemi.combank.services;

import com.dehemi.combank.dao.Transaction;
import com.dehemi.combank.dao.User;
import com.dehemi.combank.exceptions.CSVProcessException;
import com.dehemi.combank.repo.TransactionRepository;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class TransactionService {
    final TransactionRepository transactionRepository;
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Page<Transaction> getTransactions(int page, int size, String userId) {
        return transactionRepository.findAllByUserId(userId,PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    public Long getTotalTransactions() {
        return transactionRepository.count();
    }

    public void saveCSVToDB(String csv, String accountNumber, String username) throws CSVProcessException, CsvValidationException, IOException {
        List<Transaction> transactions = CSVProcessor.processTransactionToCSV(csv, accountNumber, username);
        List<Transaction> transactions1 = transactionRepository.insertAllNew(transactions.reversed());
        log.info("new transactions {}", transactions1.size());
    }
}
