package com.dehemi.combank.dao.http;

import com.dehemi.combank.dao.Transaction;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class TransactionsResponse {
    List<Transaction> transactionList;
    int totalPages;
    long totalItems;
}
