package com.pet.project.service.impl;

import com.pet.project.exception.InvalidAmountException;
import com.pet.project.exception.NullEntityReferenceException;
import com.pet.project.model.entity.Account;
import com.pet.project.repository.AccountRepository;
import com.pet.project.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    @Override
    public Account create(Account account) {
        try {
            return accountRepository.save(account);
        } catch (InvalidDataAccessApiUsageException invalidDataAccessApiUsageException) {
            throw new NullEntityReferenceException("Account cannot be 'null'");
        }
    }

    @Override
    public void replenishBalance(long id, double sum) {
        if (sum < 0.1) {
            throw new InvalidAmountException("Sum must be greater than 0.1");
        }
        Account account = readById(id);
        account.setBalance(
                account.getBalance().add(new BigDecimal(sum))
        );
        update(account);
    }

    @Override
    public Account update(Account account) {
        if (account != null) {
            readById(account.getId());
            return accountRepository.save(account);
        }
        throw new NullEntityReferenceException("Account cannot be 'null'");
    }

    @Override
    public Account readById(long id) {
        return accountRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Account with id " + id + " not found"));
    }

    @Override
    public List<Account> getAll() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.isEmpty() ? new ArrayList<>() : accounts;
    }
}
