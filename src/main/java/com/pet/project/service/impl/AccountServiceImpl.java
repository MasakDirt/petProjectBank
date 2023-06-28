package com.pet.project.service.impl;

import com.pet.project.exception.NullEntityReferenceException;
import com.pet.project.model.entity.Account;
import com.pet.project.repository.AccountRepository;
import com.pet.project.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {
   private final AccountRepository accountRepository;

    @Override
    public Account create(Account account) {
        try {
            return accountRepository.save(account);
        } catch (IllegalArgumentException illegal) {
            throw new NullEntityReferenceException("Account cannot be 'null'");
        }
    }

    @Override
    public void delete(long id) {
        Account account = readById(id);
        accountRepository.delete(account);
    }

    @Override
    public Account update(Account account) {
        if (account != null) {
            Account oldAcc = readById(account.getId());
            return accountRepository.save(account);
        }
        throw new NullEntityReferenceException("Account cannot be 'null'");
    }

    @Override
    public Account readById(long id) {
        return accountRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Account with id " + id + " not found"));
    }

    @Override
    public List<Account> getAll() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.isEmpty() ? new ArrayList<>() : accounts;
    }
}
