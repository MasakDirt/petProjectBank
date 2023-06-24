package com.pet.project.service;

import com.pet.project.model.Card;
import com.pet.project.model.Customer;

import java.util.List;

public interface CardService {
    Card create(Card card);
    void delete(long id);
    Card update(Card card);
    Card readById(long id);
    Card readByNumber(String number);
    Card readByOwner(Customer customer);
    List<Card> getAllByOwner(Customer owner);
    List<Card> getAll();
}
