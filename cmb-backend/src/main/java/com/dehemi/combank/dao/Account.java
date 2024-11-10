package com.dehemi.combank.dao;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Account {
    String accountNumber;
    String accountType;
    String availableTotal;
    String currentTotal;
}
