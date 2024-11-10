package com.dehemi.combank.services;

import com.dehemi.combank.dao.Transaction;
import com.dehemi.combank.repo.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    final TransactionRepository transactionRepository;
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Page<Transaction> getTransactions(int page, int size, String userId) {
        return transactionRepository.findAllByUserId(userId,PageRequest.of(page, size));
    }

    public Long getTotalTransactions() {
        return transactionRepository.count();
    }
}
