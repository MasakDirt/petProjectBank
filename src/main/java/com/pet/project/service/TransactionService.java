package com.pet.project.service;

import com.pet.project.model.entity.Transaction;

import java.util.List;

public interface TransactionService {
    Transaction create(Transaction transaction, int sum);
    void delete(long id);
    Transaction update(Transaction transaction);
    Transaction readById(long id);
    List<Transaction> getAll();
}
