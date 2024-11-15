package com.dehemi.combank.controller;

import com.dehemi.combank.dao.Transaction;
import com.dehemi.combank.dao.User;
import com.dehemi.combank.dao.http.TransactionsResponse;
import com.dehemi.combank.services.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("transactions")
@CrossOrigin
public class TransactionController {
    TransactionService transactionService;
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping()
    public Mono<TransactionsResponse> getTransactions(@RequestParam int page, @RequestParam int size, @RequestAttribute User user) {
        Page<Transaction> transactions = transactionService.getTransactions(page-1, size, user.getUsername());
        TransactionsResponse.TransactionsResponseBuilder builder = TransactionsResponse.builder();

        builder.transactionList(transactions.getContent());
        builder.totalPages(transactions.getTotalPages());

        return Mono.just(builder.build());
    }
}
