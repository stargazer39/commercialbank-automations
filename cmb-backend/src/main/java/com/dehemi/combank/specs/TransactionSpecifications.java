package com.dehemi.combank.specs;

import com.dehemi.combank.dao.Transaction;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

public class TransactionSpecifications {
    public static Specification<Transaction> hasUserId(String userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userId"), userId);
    }

    public static Specification<Transaction> hasTag(String tag) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("defaultTag"), tag);
    }

    public static Specification<Transaction> hasTransactionDateBetween(LocalDate fromDate, LocalDate toDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("transactionDate"), fromDate, toDate);
    }

    public static Specification<Transaction> hasAccountNumbers(List<String> accountNumbers) {
        return (root, query, criteriaBuilder) -> root.get("accountNumber").in(accountNumbers);
    }
}
