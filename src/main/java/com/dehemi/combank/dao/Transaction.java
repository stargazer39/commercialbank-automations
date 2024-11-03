package com.dehemi.combank.dao;

import com.google.common.hash.Hashing;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Entity
public class Transaction {
    @Id
    private String hash;
    @CreationTimestamp
    private Timestamp createdAt;
    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private LocalDate transactionDate;
    private String description;
    private String currency;
    private BigDecimal debit;
    private BigDecimal credit;
    private BigDecimal runningBalance;
    @Column(nullable = false)
    private String accountNumber;

    public Transaction() {

    }

    public void computeHash() {
        String hash = transactionDate +
                description +
                currency +
                debit +
                credit +
                runningBalance + accountNumber;

        this.setHash(Hashing.sha256()
                .hashString(hash, StandardCharsets.UTF_8)
                .toString());
    }
}
