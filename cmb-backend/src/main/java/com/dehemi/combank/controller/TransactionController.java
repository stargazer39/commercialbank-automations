package com.dehemi.combank.controller;

import com.dehemi.combank.dao.*;
import com.dehemi.combank.dao.http.TransactionsResponse;
import com.dehemi.combank.exceptions.CSVProcessException;
import com.dehemi.combank.services.TransactionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("transactions")
@CrossOrigin
public class TransactionController {
    TransactionService transactionService;
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping()
    public TransactionsResponse getTransactions(@RequestParam int page, @RequestParam int size, @RequestAttribute User user, @RequestParam(required = false) String tag,@RequestParam(required = false) LocalDate start, @RequestParam(required = false) LocalDate end, @RequestParam(required = false) List<String> accountNumber, @RequestParam(required = false) TransactionType type) {
        Page<Transaction> transactions = transactionService.getTransactions(page-1, size, user.getUsername(),tag,start,end,accountNumber,type);
        return TransactionsResponse
                .builder()
                .transactionList(transactions.getContent())
                .totalPages(transactions.getTotalPages())
                .totalItems(transactions.getTotalElements())
                .build();
    }

    @PostMapping("csv")
    public UploadTransactionResponse uploadTransactions(@RequestBody CSVTransactionUpload csvTransactionUpload, @RequestAttribute User user) throws CSVProcessException, CsvValidationException, IOException {
        int saved = transactionService.saveCSVToDB(csvTransactionUpload.getCsv(), csvTransactionUpload.getAccountNumber(), user.getUsername());
        return new UploadTransactionResponse(saved);
    }

    @GetMapping("debit/by-default-tag")
    public TransactionSummeryByDefaultTagResponse getTotalDebitGroupedByTag(@RequestAttribute User user, @RequestParam String start, @RequestParam String end, @RequestParam(required = false) List<String> accountNumber) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        List<TransactionSummeryByDefaultTag> totalDebitByTag = transactionService.getTotalDebitByTag(user.getUsername(), startDate, endDate, accountNumber);
        return new TransactionSummeryByDefaultTagResponse(totalDebitByTag);
    }

    @PatchMapping("{hash}")
    public void updateTransaction(@PathVariable("hash") String hash, @RequestAttribute User user, @RequestBody JsonNode transaction) {
        transactionService.updateTransactionByHash(hash,transaction, user.getUsername());
    }
}
