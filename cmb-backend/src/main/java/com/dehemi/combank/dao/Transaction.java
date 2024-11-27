package com.dehemi.combank.dao;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.google.common.hash.Hashing;
import jakarta.persistence.*;
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
@Table(uniqueConstraints =
        { //other constraints
                @UniqueConstraint(name = "UniqueDefaultTagAndHash", columnNames = { "hash", "defaultTag" })})
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
    @OneToMany(cascade= CascadeType.ALL,orphanRemoval = true,fetch=FetchType.EAGER)
    private List<TransactionTag> tags;
    @Column(nullable = false)
    private Boolean tagsGenerated = false;
    private String defaultTag;

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
