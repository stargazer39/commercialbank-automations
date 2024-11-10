package com.dehemi.combank.repo;

import com.dehemi.combank.dao.Transaction;

import java.util.List;


public interface CustomizedTransactionRepo {
    List<Transaction> insertAllNew(List<Transaction> transactions);
}
