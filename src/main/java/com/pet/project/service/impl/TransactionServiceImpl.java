package com.pet.project.service.impl;

import com.pet.project.exception.InsufficientFundsException;
import com.pet.project.exception.InvalidAmountException;
import com.pet.project.exception.NullEntityReferenceException;
import com.pet.project.model.dto.transaction.TransactionCreateRequest;
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
import java.util.List;

@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final CardService cardService;


    @Override
    public Transaction create(TransactionCreateRequest request, long accountId) {
        if (request != null) {
            double transferAmount = request.getTransferAmount();

            if (transferAmount < 0.1) {
                throw new InvalidAmountException("Sum must be greater than 0.1");
            }

            var recipientCard = cardService.readByNumber(request.getCardNumber());

            var transaction = createNewTransaction(accountId, transferAmount, recipientCard.getNumber());

            addedAndSubtractBalances(transaction, recipientCard, transferAmount);

            return transactionRepository.save(transaction);
        }
        throw new NullEntityReferenceException("Transaction cannot be 'null'");
    }

    @Override
    public void delete(long id) {
        var transaction = readById(id);
        transactionRepository.delete(transaction);
    }

    @Override
    public Transaction update(Transaction transaction) {
        if (transaction != null) {
            readById(transaction.getId());
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

    private Transaction createNewTransaction(long accountId, double transferAmount, String cardNumber) {
        var account = accountService.readById(accountId);

        if (account.getBalance().doubleValue() < transferAmount) {
            throw new InsufficientFundsException("There are not enough funds on your card " + account.getCard().getNumber() + " for the transaction");
        }

        var transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setRecipientCard(cardNumber);
        transaction.setTransferAmount(new BigDecimal(transferAmount));

        return transaction;
    }

    private void addedAndSubtractBalances(Transaction transaction, Card recipientCard, double transferAmount) {
        var account = transaction.getAccount();

        transaction.setBalanceAfter(
               account.getBalance().subtract(new BigDecimal(transferAmount))
        );

        account.setBalance(transaction.getBalanceAfter());

        recipientCard.getAccount().setBalance(
                recipientCard.getAccount().getBalance().add(new BigDecimal(transferAmount))
        );

        transaction.setFundsWithdrawn(new BigDecimal("-" + transferAmount));

        accountService.update(account);
        accountService.update(recipientCard.getAccount());
    }
}
