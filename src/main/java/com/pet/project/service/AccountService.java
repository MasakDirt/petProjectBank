package com.pet.project.service;

import com.pet.project.model.entity.Account;
import com.pet.project.model.entity.Card;
import com.pet.project.model.entity.Customer;

import java.util.List;

public interface AccountService {
    Account create(Card card, Customer owner);

    Account replenishBalance(long id, double sum);

    Account update(Account account);

    Account readById(long id);

    List<Account> getAll();
}
