package com.dehemi.combank.services;

import com.dehemi.combank.config.TimeoutConfig;
import com.dehemi.combank.config.UsersConfig;
import com.dehemi.combank.dao.Account;
import com.dehemi.combank.dao.Transaction;
import com.dehemi.combank.dao.TransactionScanLog;
import com.dehemi.combank.exceptions.CSVProcessException;
import com.dehemi.combank.repo.TransactionRepository;
import com.dehemi.combank.repo.TransactionsScanLogRepository;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CombankService {
    final private Map<String,CombankInstance> instances;
    final SeleniumService seleniumService;
    final TransactionRepository transactionRepository;
    final TransactionsScanLogRepository transactionsScanLogRepository;
    final AccountsService accountsService;
    final TimeoutConfig timeoutConfig;

    public CombankService(UsersConfig usersConfig, SeleniumService seleniumService, TransactionRepository transactionRepository, TransactionsScanLogRepository transactionsScanLogRepository,AccountsService accountsService, TimeoutConfig timeoutConfig) {
            this.seleniumService = seleniumService;
            this.transactionRepository = transactionRepository;
            this.transactionsScanLogRepository = transactionsScanLogRepository;
            this.accountsService = accountsService;
            this.timeoutConfig = timeoutConfig;

            instances = new HashMap<>();

            usersConfig.getUsers().entrySet().stream().forEach(e -> {
                instances.put(e.getKey(),new CombankInstance(e.getValue(), seleniumService.getInstance(), timeoutConfig));
            });
    }

    @Scheduled(fixedDelay = 10*1000)
    public void refresh() {
        log.info("Starting refresh");
        instances.forEach((key, instance) -> {
            try {
                log.info("Initializing instance {}", key);
                instance.init();
                log.info("Logging in instance {}", key);
                instance.login();
                log.info("Getting accounts {}", key);
                List<Account> account = instance.getAccounts();

                for (Account a : account) {
                    accountsService.processAccount(a, instance);
                }

            } catch (InterruptedException | CsvValidationException | IOException | CSVProcessException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
