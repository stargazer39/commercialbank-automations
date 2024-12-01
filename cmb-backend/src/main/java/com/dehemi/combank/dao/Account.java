package com.dehemi.combank.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.transaction.annotation.Transactional;

@Data
@AllArgsConstructor
@Entity
public class Account {
    @Id
    String accountNumber;
    String accountType;
    String availableTotal;
    String currentTotal;
    String accountName;

    public Account() {

    }
}
