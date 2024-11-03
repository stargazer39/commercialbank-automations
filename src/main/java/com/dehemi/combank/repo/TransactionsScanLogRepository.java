package com.dehemi.combank.repo;

import com.dehemi.combank.dao.Transaction;
import com.dehemi.combank.dao.TransactionScanLog;
import org.springframework.data.repository.CrudRepository;

public interface TransactionsScanLogRepository  extends CrudRepository<TransactionScanLog,Integer> {
}
