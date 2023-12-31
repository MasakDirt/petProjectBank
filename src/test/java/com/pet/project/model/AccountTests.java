package com.pet.project.model;

import com.pet.project.model.entity.Account;
import com.pet.project.model.entity.Card;
import com.pet.project.model.entity.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.pet.project.model.ValidatorForTests.getViolations;
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

        validAccount = new Account();
        validAccount.setId(1);
        validAccount.setBalance(new BigDecimal(10000));
        validAccount.setTransactions(List.of(first, second, third));
    }

    @Test
    public void test_Valid_Account() {
        Set<ConstraintViolation<Account>> violations = getViolations(validAccount);
        assertEquals(0, violations.size());
    }

    @Test
    public void test_IfDontSetAccount() {
        Account account = new Account();
        assertEquals(BigDecimal.ZERO, account.getBalance());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidAccount")
    public void test_Invalid_Account(BigDecimal input, BigDecimal error) {
        Account wrongAccount = new Account();
        wrongAccount.setId(2);
        wrongAccount.setBalance(input);
        wrongAccount.setCard(new Card());
        wrongAccount.setTransactions(List.of(new Transaction()));

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
    public void test_Zero_Account() {
        Account actual = new Account();
        actual.setId(3);
        actual.setBalance(BigDecimal.valueOf(0));
        actual.setCard(new Card());
        actual.setTransactions(List.of(new Transaction()));

        Set<ConstraintViolation<Account>> violations = getViolations(actual);
        assertEquals(0, violations.size());
    }
}
