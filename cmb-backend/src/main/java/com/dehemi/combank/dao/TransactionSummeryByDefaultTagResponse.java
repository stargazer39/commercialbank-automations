package com.dehemi.combank.dao;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TransactionSummeryByDefaultTagResponse {
    List<TransactionSummeryByDefaultTag> summery;
}
