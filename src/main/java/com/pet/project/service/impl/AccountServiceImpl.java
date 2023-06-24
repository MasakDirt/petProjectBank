package com.pet.project.service.impl;

import com.pet.project.exception.NullEntityReferenceException;
import com.pet.project.model.Account;
import com.pet.project.model.Card;
import com.pet.project.model.TransactionHistory;
import com.pet.project.repository.AccountRepository;
import com.pet.project.service.AccountService;
import com.pet.project.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    CardService cardService;

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
    public Account readByCard(long cardId) {
        Card card = cardService.readById(cardId);
        return card.getCardAccount();
    }

    @Override
    public TransactionHistory getHistory(Account account) {
        Account checkForExceptions = readById(account.getId());
        return checkForExceptions.getHistory();
    }
}
