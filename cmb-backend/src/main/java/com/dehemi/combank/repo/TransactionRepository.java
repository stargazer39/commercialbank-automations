package com.dehemi.combank.repo;

import com.dehemi.combank.dao.Transaction;
import com.dehemi.combank.dao.TransactionSummeryByDefaultTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,String>, CustomizedTransactionRepo, JpaSpecificationExecutor<Transaction> {
    Page<Transaction> findAllByUserId(String userId, Pageable pageable);
    Page<Transaction> findAllByUserIdAndDefaultTagAndTransactionDateIsBetween(String userId,String defaultTag,LocalDate start,LocalDate end, Pageable pageable);
    Page<Transaction> findAllByUserIdAndDefaultTag(String userId,String defaultTag, Pageable pageable);
    Transaction findFirstByHashAndUserId(String hash, String userId);

    @Query("SELECT t FROM Transaction t WHERE t.tagsGenerated = false OR t.tagsGenerated IS NULL")
    List<Transaction> findTagsGeneratedFalse(Pageable pageable);

    @Query("""
    SELECT
        defaultTag,
        SUM(debit) AS total_debit,
        COUNT(hash) AS total_count
    FROM
        Transaction
    WHERE
        userId = :userId AND
        accountNumber IN (:accountNumbers) AND
        transactionDate BETWEEN :start AND :end AND
        debit > 0
    GROUP BY
        defaultTag
    """)
    List<Object[]> findTotalDebitByTag(LocalDate start, LocalDate end, List<String> accountNumbers, String userId);
}