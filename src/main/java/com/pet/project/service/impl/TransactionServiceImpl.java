package com.pet.project.service.impl;

import com.pet.project.exception.NullEntityReferenceException;
import com.pet.project.model.Transaction;
import com.pet.project.repository.TransactionRepository;
import com.pet.project.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    TransactionRepository transactionRepository;

    @Override
    public Transaction create(Transaction transaction) {
        try {
            return transactionRepository.save(transaction);
        } catch (IllegalArgumentException exception) {
            throw new NullEntityReferenceException("Transaction cannot be 'null'");
        }
    }

    @Override
    public void delete(long id) {
        Transaction transaction = readById(id);
        transactionRepository.delete(transaction);
    }

    @Override
    public Transaction update(Transaction transaction) {
        if (transaction != null){
            Transaction oldTransaction = readById(transaction.getId());
            return transactionRepository.save(transaction);
        }
        throw new NullEntityReferenceException("Transaction cannot be 'null'");
    }

    @Override
    public Transaction readById(long id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        if (transaction.isPresent()) {
            return transaction.get();
        }
        throw new NoSuchElementException("Transaction with id " + id + " not found");
    }

    @Override
    public List<Transaction> getAll() {
        return transactionRepository.findAll();
    }
}
