package com.dehemi.combank.dao;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransactionSummeryByDefaultTag {
    String defaultTag;
    BigDecimal totalDebit;
    Long totalTransactions;
}
