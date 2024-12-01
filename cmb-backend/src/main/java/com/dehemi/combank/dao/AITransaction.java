package com.dehemi.combank.dao;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AITransaction {
    String hash;
    String description;
    String defaultTag;
    BigDecimal amount;

    public static AITransaction from(Transaction transaction) {
        AITransaction aiTransaction = new AITransaction();
        aiTransaction.hash = transaction.getHash();
        aiTransaction.description = transaction.getDescription();
        aiTransaction.amount = transaction.getDebit();
        return aiTransaction;
    }
}
