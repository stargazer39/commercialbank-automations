package com.dehemi.combank.repo;

import com.dehemi.combank.dao.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface AccountRepository extends JpaRepository<Account,String> {
    @Transactional
    default Account updateOrInsert(Account entity) {
        return save(entity);
    }
}
