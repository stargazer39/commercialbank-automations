package com.dehemi.combank.services;

import com.dehemi.combank.clients.OpenAI;
import com.dehemi.combank.config.TimeoutConfig;
import com.dehemi.combank.config.UsersConfig;
import com.dehemi.combank.dao.*;
import com.dehemi.combank.exceptions.CSVProcessException;
import com.dehemi.combank.repo.TransactionRepository;
import com.dehemi.combank.repo.TransactionsScanLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
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
    private final OpenAI openAI;

    public CombankService(UsersConfig usersConfig, SeleniumService seleniumService, TransactionRepository transactionRepository, TransactionsScanLogRepository transactionsScanLogRepository, AccountsService accountsService, TimeoutConfig timeoutConfig, OpenAI openAI) {
            this.seleniumService = seleniumService;
            this.transactionRepository = transactionRepository;
            this.transactionsScanLogRepository = transactionsScanLogRepository;
            this.accountsService = accountsService;
            this.timeoutConfig = timeoutConfig;

            instances = new HashMap<>();

            usersConfig.getUsers().entrySet().stream().forEach(e -> {
                instances.put(e.getKey(),new CombankInstance(e.getValue(), seleniumService.getInstance(), timeoutConfig));
            });
        this.openAI = openAI;
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
                log.info("Refresh exception for instance {}",instance.getUser(),ex);
            }
        });
    }

    @Scheduled(initialDelay = 0)
    public void generateTags() throws IOException {
        log.info("Starting generate tags");
        List<Transaction> transactionList = transactionRepository.findFirst100TagsGeneratedFalse();

        if(transactionList.isEmpty()) {
            return;
        }

        Map<String,Transaction> transactionMap = new HashMap<>();

        for (Transaction transaction : transactionList) {
            transactionMap.put(transaction.getHash(),transaction);
        }

        List<AITransaction> aiTransactions = openAI.categorizeTransactions(transactionList);

        for(AITransaction aiTransaction : aiTransactions) {
            Transaction transaction = transactionMap.get(aiTransaction.getHash());
            transaction.setDefaultTag(aiTransaction.getDefaultTag());
            transaction.setTagsGenerated(true);
        }

        transactionRepository.saveAll(transactionList);

//
//        for (Transaction t : transactionList) {
//            TransactionTag transactionTag = new TransactionTag();
//            transactionTag.setAi(true);
//            transactionTag.setTag("ai");
////            transactionTag.setHash(t.getHash());
//
//            t.setTags(List.of());
//        }
//
//        transactionRepository.saveAll(transactionList);
    }
}
