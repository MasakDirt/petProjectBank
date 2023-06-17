package com.pet.project.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static com.pet.project.model.ValidatorForTests.getViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CardTests {
    private static Card validCard;
    @BeforeAll
    static void setUp(){
        validCard = new Card();
        validCard.setId(1);
        validCard.setCardAccount(new Account());
        validCard.setOwner(new Customer());
    }

    @Test
    public void checkValidCard(){
        Set<ConstraintViolation<Card>> violations = getViolations(validCard);
        assertEquals(0, violations.size());
    }
}
