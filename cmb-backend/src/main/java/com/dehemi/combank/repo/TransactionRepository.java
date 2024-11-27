package com.dehemi.combank.repo;

import com.dehemi.combank.dao.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,String>, CustomizedTransactionRepo {
    Page<Transaction> findAllByUserId(String userId, Pageable pageable);
    // Alternatively, using JPQL
    @Query("SELECT t FROM Transaction t WHERE t.tagsGenerated = false OR t.tagsGenerated IS NULL")
    List<Transaction> findFirst100TagsGeneratedFalse();
}
