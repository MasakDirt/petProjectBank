package com.pet.project.service.impl;

import com.pet.project.exception.NullEntityReferenceException;
import com.pet.project.model.Transaction;
import com.pet.project.model.TransactionHistory;
import com.pet.project.repository.TransactionHistoryRepository;
import com.pet.project.service.TransactionHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionHistoryServiceImpl implements TransactionHistoryService {
    @Autowired
    TransactionHistoryRepository transactionHistoryRepository;

    @Override
    public TransactionHistory create(TransactionHistory history) {
        try {
            return transactionHistoryRepository.save(history);
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new NullEntityReferenceException("TransactionHistory cannot be 'null'");
        }
    }

    @Override
    public void delete(long id) {
        TransactionHistory history = readById(id);
        transactionHistoryRepository.delete(history);
    }

    @Override
    public TransactionHistory update(TransactionHistory history) {
        if (history != null){
            TransactionHistory oldHistory = readById(history.getId());
            return transactionHistoryRepository.save(history);
        }
        throw new NullEntityReferenceException("TransactionHistory cannot be 'null'");
    }

    @Override
    public TransactionHistory readById(long id) {
        Optional<TransactionHistory> history = transactionHistoryRepository.findById(id);
        if (history.isPresent()){
            return history.get();
        }
        throw new EntityNotFoundException("TransactionalHistory with id " + id + " not found");
    }

    @Override
    public List<TransactionHistory> getAll() {
        return transactionHistoryRepository.findAll();
    }

    @Override
    public List<Transaction> getHistory(long id) {
        TransactionHistory history = readById(id);
        return history.getTransactions();
    }
}
