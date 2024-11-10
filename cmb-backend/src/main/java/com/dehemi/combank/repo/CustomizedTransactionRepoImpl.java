package com.dehemi.combank.repo;

import com.dehemi.combank.dao.Transaction;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomizedTransactionRepoImpl implements CustomizedTransactionRepo {
    private final EntityManager em;

    @Autowired
    public CustomizedTransactionRepoImpl(JpaContext context) {
        this.em = context.getEntityManagerByManagedType(Transaction.class);
    }

    @Transactional
    public void upsertTransaction(Transaction transaction) {
        Transaction oldT = em.find(Transaction.class, transaction.getHash());
        if(oldT == null) {
            return;
        }

        em.persist(transaction);
    }

    @Override
    @Transactional
    public List<Transaction> insertAllNew(List<Transaction> transactions) {
        List<Transaction> newTransactions = new ArrayList<>();

        for(Transaction transaction : transactions) {
            Transaction oldT = em.find(Transaction.class, transaction.getHash());
            if(oldT != null) {
                continue;
            }

            em.persist(transaction);
            newTransactions.add(transaction);
        }

        return  newTransactions;
    }
}
