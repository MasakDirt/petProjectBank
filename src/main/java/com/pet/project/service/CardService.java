package com.pet.project.service;

import com.pet.project.model.entity.Card;
import com.pet.project.model.entity.Customer;
import com.pet.project.model.entity.Transaction;

import java.util.List;

public interface CardService {
    Card create(Card card);
    void delete(long id);
    Card update(Card card);
    Card readById(long id);
    Card readByNumber(String number);
    Card readByOwner(Customer customer, long id);
    List<Card> getAllByOwner(Customer owner);
    List<Card> getAll();
    List<Transaction> getHistory(long id);
}
