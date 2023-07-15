package com.pet.project.model;

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
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

import static com.pet.project.model.ValidatorForTests.getViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TransactionTests {
    private static Transaction validTransaction;

    @BeforeAll
    static void setUp() {
        Card card = new Card();
        validTransaction = new Transaction();
        validTransaction.setId(1);
        validTransaction.setRecipientCard(card.getNumber());
        validTransaction.setBalanceAfter(BigDecimal.valueOf(0));
        validTransaction.setTransferAmount(BigDecimal.valueOf(0.1));
        validTransaction.setFundsWithdrawn(BigDecimal.valueOf(-1));
    }

    @Test
    public void test_Valid_Transaction() {
        Set<ConstraintViolation<Transaction>> violations = getViolations(validTransaction);
        assertEquals(0, violations.size());
    }

    @Test
    public void test_DataCreating() {
        assertEquals(LocalDate.now(), validTransaction.getDoneAt().toLocalDate());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidTransferAmount")
    public void test_Invalid_TransferAmount(BigDecimal transferAmount, BigDecimal error) {
        Transaction transaction = new Transaction();
        transaction.setBalanceAfter(BigDecimal.valueOf(0));
        transaction.setFundsWithdrawn(BigDecimal.valueOf(-0.1));
        transaction.setRecipientCard("7835 7138 5614 4656");

        transaction.setTransferAmount(transferAmount);

        Set<ConstraintViolation<Transaction>> violations = getViolations(transaction);

        assertEquals(1, violations.size());
        assertEquals(error, violations.iterator().next().getInvalidValue());
    }

    private static Stream<Arguments> provideInvalidTransferAmount() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(0), BigDecimal.valueOf(0)),
                Arguments.of(BigDecimal.valueOf(-0.1), BigDecimal.valueOf(-0.1)),
                Arguments.of(BigDecimal.valueOf(-17), BigDecimal.valueOf(-17)),
                Arguments.of(BigDecimal.valueOf(-100), BigDecimal.valueOf(-100)),
                Arguments.of(null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidBalanceAfter")
    public void test_Invalid_balanceAfter(BigDecimal balanceAfter, BigDecimal error) {
        Transaction transaction = new Transaction();
        transaction.setTransferAmount(BigDecimal.valueOf(0.1));
        transaction.setFundsWithdrawn(BigDecimal.valueOf(-0.1));
        transaction.setRecipientCard("7835 7138 5614 4656");

        transaction.setBalanceAfter(balanceAfter);

        Set<ConstraintViolation<Transaction>> violations = getViolations(transaction);

        assertEquals(1, violations.size());
        assertEquals(error, violations.iterator().next().getInvalidValue());
    }

    private static Stream<Arguments> provideInvalidBalanceAfter() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(-0.1), BigDecimal.valueOf(-0.1)),
                Arguments.of(BigDecimal.valueOf(-134), BigDecimal.valueOf(-134)),
                Arguments.of(BigDecimal.valueOf(-8), BigDecimal.valueOf(-8)),
                Arguments.of(null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidFundsWithdrawn")
    public void test_Invalid_FundsWithdrawn(BigDecimal fundsWithdrawn, BigDecimal error) {
        Transaction transaction = new Transaction();
        transaction.setBalanceAfter(BigDecimal.valueOf(0));
        transaction.setTransferAmount(BigDecimal.valueOf(0.1));
        transaction.setRecipientCard("7835 7138 5614 4656");

        transaction.setFundsWithdrawn(fundsWithdrawn);

        Set<ConstraintViolation<Transaction>> violations = getViolations(transaction);

        assertEquals(1, violations.size());
        assertEquals(error, violations.iterator().next().getInvalidValue());
    }

    private static Stream<Arguments> provideInvalidFundsWithdrawn() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.1)),
                Arguments.of(BigDecimal.valueOf(127), BigDecimal.valueOf(127)),
                Arguments.of(BigDecimal.valueOf(10000), BigDecimal.valueOf(10000)),
                Arguments.of(null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidRecipientCard")
    public void test_Invalid_RecipientCard(String recipientCard, String error) {
        Transaction transaction = new Transaction();
        transaction.setBalanceAfter(BigDecimal.valueOf(0));
        transaction.setTransferAmount(BigDecimal.valueOf(0.1));
        transaction.setFundsWithdrawn(BigDecimal.valueOf(-0.1));


        transaction.setRecipientCard(recipientCard);

        Set<ConstraintViolation<Transaction>> violations = getViolations(transaction);

        assertEquals(1, violations.size());
        assertEquals(error, violations.iterator().next().getInvalidValue());
    }

    private static Stream<Arguments> provideInvalidRecipientCard() {
        return Stream.of(
                Arguments.of("", ""),
                Arguments.of(null, null)
        );
    }
}
