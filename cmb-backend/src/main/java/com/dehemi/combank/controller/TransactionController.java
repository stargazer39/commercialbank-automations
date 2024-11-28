package com.dehemi.combank.controller;

import com.dehemi.combank.dao.CSVTransactionUpload;
import com.dehemi.combank.dao.Transaction;
import com.dehemi.combank.dao.User;
import com.dehemi.combank.dao.http.TransactionsResponse;
import com.dehemi.combank.exceptions.CSVProcessException;
import com.dehemi.combank.services.TransactionService;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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

    @PostMapping("csv")
    public void uploadTransactions(@RequestBody CSVTransactionUpload csvTransactionUpload, @RequestAttribute User user) throws CSVProcessException, CsvValidationException, IOException {
        transactionService.saveCSVToDB(csvTransactionUpload.getCsv(), csvTransactionUpload.getAccountNumber(), user.getUsername());
    }
}
