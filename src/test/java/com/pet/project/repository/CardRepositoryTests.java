package com.pet.project.repository;

import com.pet.project.model.entity.Account;
import com.pet.project.model.entity.Card;
import com.pet.project.model.entity.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

//In Repository layer I check only my methods!
@ActiveProfiles("test")
@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
public class CardRepositoryTests {
    private final CustomerRepository customerRepository;
    private final CardRepository cardRepository;
    private static int idCounter = 6;

    @Autowired
    public CardRepositoryTests(CustomerRepository customerRepository, CardRepository cardRepository) {
        this.customerRepository = customerRepository;
        this.cardRepository = cardRepository;
    }
    @Test
    void injectedComponentsAreNotNull() {
        assertThat(customerRepository).isNotNull();
        assertThat(cardRepository).isNotNull();
    }

    @Test
    public void checkFindCardByOwnerAndId() {
        Customer owner = customerRepository.getOne(1L);

        Card expected = createNewCard();

        cardRepository.save(expected);

        Card actual = cardRepository.findCardByOwnerAndId(owner, expected.getId())
                .orElseThrow(() -> new NoSuchElementException("We can not find card with owner " + owner + " and id " + expected.getId()));

        assertEquals(expected, actual, "Here cards need to be equals");
    }

    @Test
    public void checkFindCardByNumber() {
        Card expected = createNewCard();
        cardRepository.save(expected);

        Card actual = cardRepository.findCardByNumber(expected.getNumber())
                .orElseThrow(() -> new NoSuchElementException("We can not find card with number: " + expected.getNumber()));

        assertEquals(expected, actual, "Here cards need to be equals");
    }

    private Card createNewCard() {
        Customer owner = customerRepository.getOne(1L);

        Account account = new Account();
        account.setBalance(new BigDecimal(20000));
        account.setId(20L);

        Card card = new Card();
        card.setId(idCounter++);
        card.setOwner(owner);
        card.setAccount(account);

        return card;
    }
}
