package com.pet.project.service;

import com.pet.project.exception.InvalidAmountException;
import com.pet.project.exception.NullEntityReferenceException;
import com.pet.project.model.entity.Account;
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
public class AccountServiceTests {
    private final CustomerService customerService;
    private final CardService cardService;
    private final AccountService accountService;
    private List<Account> accounts;

    @Autowired
    public AccountServiceTests(CustomerService customerService, CardService cardService, AccountService accountService) {
        this.customerService = customerService;
        this.cardService = cardService;
        this.accountService = accountService;
    }

    @BeforeEach
    public void init() {
        accounts = accountService.getAll();
    }


    @Test
    void injectedComponentsAreNotNull() {
        assertThat(customerService).isNotNull();
        assertThat(cardService).isNotNull();
        assertThat(accountService).isNotNull();
    }

    @Test
    public void test_GetAll() {
        assertAll(
                () -> assertTrue(accountService.getAll().size() > 0,
                        "There must be more than 0 accounts."),

                () -> assertEquals(accounts.size(), accountService.getAll().size(),
                        "Your getAll method don`t function well, check it!")
        );
    }

    @Test
    public void test_AccountCreate() {
        Account account = accountService.create(cardService.readById(2L), customerService.readById(2L));

        assertAll(
                () -> assertEquals(account, accountService.readById(account.getId()),
                        "Accounts must be equal!"),

                () -> assertTrue(accounts.size() < accountService.getAll().size(),
                        "Account not create, please check why.")
        );
    }

    @Test
    public void test_Invalid_AccountCreating() {
        assertThrows(NullEntityReferenceException.class, () -> accountService.create(null, null),
                "There need to be NullEntityReferenceException because we are pass null.");
    }

    @Test
    public void test_ReplenishBalanceAccount() {
        Account expected = accountService.readById(3L);
        BigDecimal balanceBefore = expected.getBalance();

        double sum = 200000;
        accountService.replenishBalance(expected.getId(), sum);

        Account actual = accountService.readById(expected.getId());

        assertThat(actual.getBalance()).isNotEqualTo(balanceBefore);
        assertThat(actual.getBalance())
                .isEqualTo(balanceBefore.add(new BigDecimal(sum)));
        assertThat(actual.getCard()).isEqualTo(expected.getCard());

    }

    @Test
    public void test_Invalid_ReplenishBalanceAccount() {
        assertAll(
                () -> assertThrows(InvalidAmountException.class, () -> accountService.replenishBalance(4L, -12),
                        "Sum must be greater than 0.1, so we have InvalidAmountException"),

                () -> assertThrows(EntityNotFoundException.class, () -> accountService.replenishBalance(4000000L, 12),
                        "There might be EntityNotFoundException because we have not account with id 4000000")
        );
    }

    @Test
    public void test_UpdateMethod() {

        Account actual = new Account();
        actual.setId(4L);
        actual.setBalance(new BigDecimal(200000));
        actual.setCard(cardService.readById(5L));
        actual.getCard().setOwner(customerService.readById(1L));

        cardService.update(actual.getCard());
        accountService.update(actual);

        Account updatedAcc = accountService.readById(4L);

        assertAll(
                () -> assertEquals(actual, updatedAcc, "Check why your account do not update."),
                () -> assertEquals(accounts.size(), accountService.getAll().size(), "Was created or deleted new Account, check why.")
        );
    }


    @Test
    public void test_Invalid_UpdateMethod() {
        Account actual = new Account();
        actual.setId(100000L);
        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> accountService.update(actual),
                        "There should be EntityNotFoundException because in DB we haven`t account with this id!"),

                () -> assertThrows(EntityNotFoundException.class, () -> accountService.update(new Account()),
                        "There should be EntityNotFoundException because in DB we haven`t this account!"),

                () -> assertThrows(NullEntityReferenceException.class, () -> accountService.update(null),
                        "There should be NullEntityReferenceException because we don`t need null in update method.")
        );
    }

    @Test
    public void test_ReadByIdMethod() {
        Transaction transaction = new Transaction();
        transaction.setTransferAmount(BigDecimal.valueOf(0.1));
        transaction.setFundsWithdrawn(BigDecimal.valueOf(-0.1));
        transaction.setBalanceAfter(BigDecimal.valueOf(0));
        transaction.setRecipientCard("7835 7138 5614 4656");

        Account expected = new Account();
        expected.setId(3);
        expected.setBalance(new BigDecimal(20));
        expected.setTransactions(List.of(transaction));
        expected.setCard(cardService.readById(1L));
        accountService.update(expected);

        Account actual = accountService.readById(expected.getId());
        assertEquals(expected, actual, "Your two accounts not equals, check please why.");
    }

    @Test
    public void test_Invalid_ReadByIdMethod() {
        assertThrows(EntityNotFoundException.class, () -> accountService.readById(20000L),
                "There might be EntityNotFoundException because we have not account with id 20000");
    }
}
