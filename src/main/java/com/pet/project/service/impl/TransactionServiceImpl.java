package com.pet.project.service.impl;

import com.pet.project.exception.NullEntityReferenceException;
import com.pet.project.model.entity.Transaction;
import com.pet.project.repository.TransactionRepository;
import com.pet.project.service.AccountService;
import com.pet.project.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    TransactionRepository transactionRepository;
    AccountService accountService;

    @Override
    public Transaction create(Transaction transaction, int sum) {
        try {

            transaction.setBalanceAfter(new BigDecimal(
                    transaction.getAccount().getBalance().subtract(BigInteger.valueOf(sum)))
            );

            transaction.getAccount().setBalance(transaction.getBalanceAfter());

            transaction.getRecipientCard().getAccount().setBalance(new BigDecimal(
                    transaction.getRecipientCard().getAccount().getBalance().add(BigInteger.valueOf(sum)))
            );

            accountService.update(transaction.getAccount());
            accountService.update(transaction.getRecipientCard().getAccount());


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
        if (transaction != null) {
            Transaction oldTransaction = readById(transaction.getId());
            return transactionRepository.save(transaction);
        }
        throw new NullEntityReferenceException("Transaction cannot be 'null'");
    }

    @Override
    public Transaction readById(long id) {
        return transactionRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Transaction with id " + id + " not found"));
    }

    @Override
    public List<Transaction> getAll() {
        return transactionRepository.findAll();
    }
}
