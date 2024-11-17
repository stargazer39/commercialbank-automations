package com.dehemi.combank.controller;

import com.dehemi.combank.dao.Transaction;
import com.dehemi.combank.dao.User;
import com.dehemi.combank.dao.http.TransactionsResponse;
import com.dehemi.combank.services.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("transactions")
@CrossOrigin
public class TransactionController {
    TransactionService transactionService;
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping()
    public TransactionsResponse getTransactions(@RequestParam int page, @RequestParam int size, @RequestAttribute User user) {
        Page<Transaction> transactions = transactionService.getTransactions(page-1, size, user.getUsername());
        return TransactionsResponse
                .builder()
                .transactionList(transactions.getContent())
                .totalPages(transactions.getTotalPages())
                .build();
    }
}
