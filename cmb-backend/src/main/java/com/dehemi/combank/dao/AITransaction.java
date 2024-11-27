package com.dehemi.combank.dao;

import lombok.Data;

@Data
public class AITransaction {
    String hash;
    String description;
    String defaultTag;

    public static AITransaction from(Transaction transaction) {
        AITransaction aiTransaction = new AITransaction();
        aiTransaction.hash = transaction.getHash();
        aiTransaction.description = transaction.getDescription();
        aiTransaction.defaultTag = transaction.getDefaultTag();
        return aiTransaction;
    }
}
