package com.pet.project.service;

import com.pet.project.model.entity.Transaction;
import com.pet.project.model.entity.TransactionHistory;

import java.util.List;

public interface TransactionHistoryService {
    TransactionHistory create(TransactionHistory history);
    void delete(long id);
    TransactionHistory update(TransactionHistory history);
    TransactionHistory readById(long id);
    List<TransactionHistory> getAll();
     List<Transaction> getHistory(long id);
}
