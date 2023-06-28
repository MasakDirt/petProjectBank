package com.pet.project.model;

import com.pet.project.model.entity.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import java.time.LocalDate;
import java.util.Set;

import static com.pet.project.model.ValidatorForTests.getViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TransactionTests {
    private static Transaction validTransaction;

    @BeforeAll
    static void setUp(){
        validTransaction = new Transaction();
        validTransaction.setId(1);
    }

    @Test
    public void checkValidTransaction(){
        Set<ConstraintViolation<Transaction>> violations = getViolations(validTransaction);
        assertEquals(0, violations.size());
    }
    @Test
    public void checkDataCreating(){
        assertEquals(LocalDate.now(), validTransaction.getDoneAt().toLocalDate());
    }
}
