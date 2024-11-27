package com.dehemi.combank.dao;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class TransactionTag {
    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String tag;
    private Boolean ai;
}

