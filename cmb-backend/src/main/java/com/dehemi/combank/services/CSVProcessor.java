package com.dehemi.combank.services;

import com.dehemi.combank.dao.Transaction;
import com.dehemi.combank.exceptions.CSVProcessException;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CSVProcessor {
    public static List<Transaction> processTransactionToCSV(String csv, String accountNumber, String username) throws CsvValidationException, IOException, CSVProcessException {
        CSVReader reader = new CSVReader(new StringReader(csv));

        int pos = -1;
        List<Transaction> transactions = new ArrayList<>();

        while (true) {
            String[] list = reader.readNext();
            pos++;

            if(list == null || list.length == 0) {
                break;
            }

            if(pos <= 2) {
                continue;
            }

            if(list[1].isEmpty() && list[2].toLowerCase(Locale.ROOT).contains("generate")) {
//                if(list[8].isEmpty() && !account.getAccountType().contains("credit-card")) { // Index 8 can be empty in credit card reports.
//                    throw new CSVProcessException();
//                }
//                if(list[2].toLowerCase(Locale.ROOT).contains("generate")) {
//                    break;
//                }
                break;
//                throw new CSVProcessException();
            }

            Transaction transaction = new Transaction();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            transaction.setTransactionDate(LocalDate.parse(list[1], formatter));
            transaction.setDescription(list[3]);
            transaction.setCurrency(list[5]);

            if (list[6] != null && !list[6].isEmpty()) {
                transaction.setDebit(new BigDecimal(list[6].replace(",", "")));
            }

            if (list[7] != null && !list[7].isEmpty()) {
                transaction.setCredit((new BigDecimal(list[7].replace(",", ""))));
            }

            if (list[8] != null && !list[8].isEmpty()) {
                transaction.setRunningBalance(new BigDecimal(list[8].replace(",", "")));
            }

            transaction.setAccountNumber(accountNumber);
            transaction.setUserId(username);
            transaction.computeHash();
            transactions.add(transaction);
        }

        return transactions;
    }
}
