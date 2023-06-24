package com.pet.project.service;

import com.pet.project.model.Account;
import com.pet.project.model.TransactionHistory;

public interface AccountService {
    Account create(Account account);
    void delete(long id);
    Account update(Account account);
    Account readById(long id);
    Account readByCard(long cardId);
    TransactionHistory getHistory(Account account);
}
