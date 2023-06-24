package com.pet.project.service;

import com.pet.project.model.Transaction;

import java.util.List;

public interface TransactionService {
    Transaction create(Transaction transaction);
    void delete(long id);
    Transaction update(Transaction transaction);
    Transaction readById(long id);
    List<Transaction> getAll();
}
