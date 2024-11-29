package com.dehemi.combank.services;

import com.dehemi.combank.dao.Transaction;
import com.dehemi.combank.dao.TransactionSummeryByDefaultTag;
import com.dehemi.combank.dao.User;
import com.dehemi.combank.exceptions.CSVProcessException;
import com.dehemi.combank.repo.TransactionRepository;
import com.dehemi.combank.specs.TransactionSpecifications;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransactionService {
    final TransactionRepository transactionRepository;
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Page<Transaction> getTransactions(int page, int size, String userId, String tag, LocalDate fromDate, LocalDate toDate, List<String> accountNumber) {
        Specification<Transaction> spec = Specification.where(TransactionSpecifications.hasUserId(userId));

        if (tag != null) {
            spec = spec.and(TransactionSpecifications.hasTag(tag));
        }
        if (fromDate != null && toDate != null) {
            spec = spec.and(TransactionSpecifications.hasTransactionDateBetween(fromDate, toDate));
        }

        if(accountNumber != null && !accountNumber.isEmpty()) {
            spec = spec.and(TransactionSpecifications.hasAccountNumbers(accountNumber));
        }

        return transactionRepository.findAll(spec, PageRequest.of(page, size, Sort.by("transactionDate").descending()));
    }

    public Long getTotalTransactions() {
        return transactionRepository.count();
    }

    public int saveCSVToDB(String csv, String accountNumber, String username) throws CSVProcessException, CsvValidationException, IOException {
        List<Transaction> transactions = CSVProcessor.processTransactionToCSV(csv, accountNumber, username);
        List<Transaction> transactions1 = transactionRepository.insertAllNew(transactions.reversed());
        log.info("new transactions {}", transactions1.size());
        return transactions1.size();
    }

    public List<TransactionSummeryByDefaultTag> getTotalDebitByTag(String userId, LocalDate start, LocalDate end, List<String> accountNumber) {
        return transactionRepository.findTotalDebitByTag(start, end, accountNumber, userId).stream().map(result -> new TransactionSummeryByDefaultTag(
                (String) result[0],
                (BigDecimal) result[1],
                (Long) result[2]
        )).collect(Collectors.toList());
    }
}
