package com.dehemi.combank.dao;

import lombok.Data;

@Data
public class CSVTransactionUpload {
    private String csv;
    private String accountNumber;
}
