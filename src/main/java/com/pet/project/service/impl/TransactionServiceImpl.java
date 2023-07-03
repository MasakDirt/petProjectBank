package com.pet.project.service.impl;

import com.pet.project.exception.InsufficientFundsException;
import com.pet.project.exception.InvalidAmountException;
import com.pet.project.exception.NullEntityReferenceException;
import com.pet.project.model.entity.Card;
import com.pet.project.model.entity.Transaction;
import com.pet.project.repository.TransactionRepository;
import com.pet.project.service.AccountService;
import com.pet.project.service.CardService;
import com.pet.project.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final CardService cardService;


    @Override
    public Transaction create(Transaction transaction, double sum) {
        if (sum < 0.1) {
            throw new InvalidAmountException("Sum must be greater than 0.1");
        }
        try {
            Card recipientCard = cardService.readByNumber(transaction.getRecipientCard());

            if (recipientCard.getAccount().getBalance().doubleValue() < sum) {
                throw new InsufficientFundsException("There are not enough funds on your card " + recipientCard.getNumber() + " for the transaction");
            }

            addedAndSubtractBalances(transaction, recipientCard, sum);

            accountService.update(transaction.getAccount());
            accountService.update(recipientCard.getAccount());

            return transactionRepository.save(transaction);
        } catch (NullPointerException nullPointerException) {
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
                new EntityNotFoundException("Transaction with id " + id + " not found"));
    }

    @Override
    public List<Transaction> getAll() {
        return transactionRepository.findAll();
    }

    private void addedAndSubtractBalances(Transaction transaction, Card recipientCard, double sum) {
        transaction.setBalanceAfter(new BigDecimal(
                transaction.getAccount().getBalance().subtract(BigInteger.valueOf((long) sum)))
        );

        transaction.getAccount().setBalance(transaction.getBalanceAfter());

        recipientCard.getAccount().setBalance(new BigDecimal(
                recipientCard.getAccount().getBalance().add(BigInteger.valueOf((long) sum)))
        );
    }
}
