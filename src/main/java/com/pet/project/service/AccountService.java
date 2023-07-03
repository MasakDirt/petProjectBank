package com.pet.project.service;

import com.pet.project.model.entity.Account;

import java.util.List;

public interface AccountService {
    Account create(Account account);

    Account update(Account account);

    Account readById(long id);

    List<Account> getAll();
}
