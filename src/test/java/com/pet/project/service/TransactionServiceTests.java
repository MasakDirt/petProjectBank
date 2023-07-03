package com.pet.project.service;

import com.pet.project.exception.InsufficientFundsException;
import com.pet.project.exception.InvalidAmountException;
import com.pet.project.exception.NullEntityReferenceException;
import com.pet.project.model.entity.Account;
import com.pet.project.model.entity.Card;
import com.pet.project.model.entity.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class TransactionServiceTests {
    private final TransactionService transactionService;
    private final CardService cardService;
    private final AccountService accountService;

    private List<Transaction> transactions;

    @Autowired
    public TransactionServiceTests(TransactionService transactionService, CardService cardService, AccountService accountService) {
        this.transactionService = transactionService;
        this.cardService = cardService;
        this.accountService = accountService;
    }

    @BeforeEach
    public void init() {
        transactions = transactionService.getAll();
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(transactionService).isNotNull();
        assertThat(cardService).isNotNull();
        assertThat(accountService).isNotNull();
    }

    @Test
    public void checkGetAll() {
        assertAll(
                () -> assertTrue(transactionService.getAll().size() > 0,
                        "There must be more than 0 transaction."),

                () -> assertEquals(transactions.size(), transactionService.getAll().size(),
                        "Your getAll method don`t function well, check it!")
        );
    }

    @Test
    public void checkCreateTransaction() {
        Card recipient = cardService.readById(3L);
        Account account = accountService.readById(4L);

        int sum = 100;

        BigDecimal recipientBalance = recipient.getAccount().getBalance();
        BigDecimal accountFromWhichCreatePaymentBalance = account.getBalance();

        Transaction transaction = new Transaction();
        transaction.setRecipientCard(recipient.getNumber());
        transaction.setAccount(account);
        transaction.setId(11L);

        transactionService.create(transaction, sum);

        assertAll(
                () -> assertEquals(cardService.readById(recipient.getId()).getAccount().getBalance(), recipientBalance.add(new BigDecimal(sum)),
                        "Recipient card not added sum to it`s balance, please check why it was"),

                () -> assertEquals(accountService.readById(account.getId()).getBalance(),
                        accountFromWhichCreatePaymentBalance.subtract(new BigDecimal(sum)),
                        "Account from which transaction was create not subtract sum, please check why it was"),

                () -> assertTrue(transactions.size() < transactionService.getAll().size(),
                        "Transaction not create, please check why.")
        );
    }

    @Test
    public void checkNotValidCreateTransaction() {
        int sum = 100;

        Transaction transaction = createTransaction();
        transaction.setId(11L);

        assertAll(
                () -> assertThrows(NullEntityReferenceException.class, () -> transactionService.create(null, sum),
                        "There need to be NullEntityReferenceException because we are pass null."),

                () -> assertThrows(InsufficientFundsException.class, () -> transactionService.create(transaction, 100000),
                        "There need to be InsufficientFundsException because we are pass sum that account balance has not."),

                () -> assertThrows(InvalidAmountException.class, () -> transactionService.create(transaction, 0),
                        "There need to be InvalidAmountException because we are pass sum that we cannot transfer."),

                () -> assertThrows(NullEntityReferenceException.class, () -> transactionService.create(new Transaction(), sum),
                        "There need to be NullEntityReferenceException because we are pass object without tabular values.")
        );
    }

    @Test
    public void checkReadByIdTransaction() {
        Transaction transaction = createTransaction();
        transaction.setId(12L);
        transactionService.create(transaction, 150);

        assertEquals(transaction, transactionService.readById(transaction.getId()),
                "Transactions need to be equals, if it isn`t, please check transactionId");
    }

    @Test
    public void checkInvalidReadByIdTransaction() {
        assertThrows(EntityNotFoundException.class, () -> transactionService.readById(10000L),
                "There must be EntityNotFoundException because we have not transaction with id 10000");
    }

    @Test
    public void checkUpdateTransaction() {
        Transaction expected = createTransaction();
        expected.setId(9L);

        transactionService.update(expected);

        assertEquals(expected, transactionService.readById(expected.getId()),
                "There must be equal transactions after updating one!");
    }

    @Test
    public void checkInvalidUpdateTransaction() {
        Transaction invalidTransaction = new Transaction();
        invalidTransaction.setId(140000L);
        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> transactionService.update(invalidTransaction),
                        "There should be EntityNotFoundException because in DB we haven`t transaction with this id!"),

                () -> assertThrows(EntityNotFoundException.class, () -> transactionService.update(new Transaction()),
                        "There should be EntityNotFoundException because in DB we haven`t transaction!"),

                () -> assertThrows(NullEntityReferenceException.class, () -> transactionService.update(null),
                        "There should be NullEntityReferenceException because we don`t need null in update method.")
        );
    }

    @Test
    public void checkDeleteTransaction() {
        transactionService.delete(2L);

        assertTrue(transactions.size() > transactionService.getAll().size(),
                "Transactions size must be smaller than transactionService.getAll() size after deleting role");
    }

    private Transaction createTransaction() {
        Card recipient = cardService.readById(3L);
        Account account = accountService.readById(4L);

        Transaction transaction = new Transaction();
        transaction.setRecipientCard(recipient.getNumber());
        transaction.setAccount(account);

        return transaction;
    }
}
