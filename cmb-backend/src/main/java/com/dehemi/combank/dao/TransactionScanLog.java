package com.dehemi.combank.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Transient
    private ObjectMapper mapper = new ObjectMapper();

    public String toString()  {
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
