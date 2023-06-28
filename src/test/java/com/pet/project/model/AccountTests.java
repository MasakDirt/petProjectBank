package com.pet.project.model;

import com.pet.project.model.entity.Account;
import com.pet.project.model.entity.Transaction;
import com.pet.project.model.entity.TransactionHistory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

import static com.pet.project.model.ValidatorForTests.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AccountTests {
    private static Account validAccount;

    @BeforeAll
    static void setUp() {
        Transaction first = new Transaction();
        first.setId(1);

        Transaction second = new Transaction();
        first.setId(2);

        Transaction third = new Transaction();
        first.setId(3);

        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setTransactions(new ArrayList<>(Arrays.asList(first, second, third)));

        validAccount = new Account();
        validAccount.setId(1);
        validAccount.setBalance(new BigDecimal(10000));
        validAccount.setHistory(transactionHistory);
    }

    @Test
    public void checkValidAccount() {
        Set<ConstraintViolation<Account>> violations = getViolations(validAccount);
        assertEquals(0, violations.size());
    }

    @Test
    public void checkIfDontSetAccount() {
        Account account = new Account();
        assertEquals(BigInteger.ZERO, account.getBalance());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidAccount")
    public void checkInvalidAccount(BigDecimal input, BigDecimal error) {
        Account wrongAccount = new Account();
        wrongAccount.setId(2);
        wrongAccount.setBalance(input);

        Set<ConstraintViolation<Account>> violations = getViolations(wrongAccount);
        assertEquals(1, violations.size());
        assertEquals(error, violations.iterator().next().getInvalidValue());
    }

    private static Stream<Arguments> provideInvalidAccount() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(-12), BigDecimal.valueOf(-12)),
                Arguments.of(BigDecimal.valueOf(-122345.4576), BigDecimal.valueOf(-122345.4576)),
                Arguments.of(null, null)
        );
    }

    @Test
    public void checkZeroAccount(){
        Account actual = new Account();
        actual.setId(3);
        actual.setBalance(BigDecimal.valueOf(0));

        Set<ConstraintViolation<Account>> violations = getViolations(actual);
        assertEquals(0, violations.size());
    }
}
