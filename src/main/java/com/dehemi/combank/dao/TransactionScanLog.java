package com.dehemi.combank.dao;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Data
public class TransactionScanLog {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @Column(nullable = false)
    String accountNumber;
    @Column(nullable = false)
    String userId;
    @CreationTimestamp
    private Timestamp createdAt;
    @Column(nullable = false)
    private Integer newLogs;
}
