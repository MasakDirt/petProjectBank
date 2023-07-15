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

import javax.persistence.EntityNotFoundException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

//In Repository layer I check only my methods!
@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class CardRepositoryTests {
    private final CustomerRepository customerRepository;
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public CardRepositoryTests(CustomerRepository customerRepository, CardRepository cardRepository, AccountRepository accountRepository) {
        this.customerRepository = customerRepository;
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(customerRepository).isNotNull();
        assertThat(cardRepository).isNotNull();
        assertThat(accountRepository).isNotNull();
    }

    @Test
    public void test_FindCardByOwnerAndId() {
        Customer owner = customerRepository.getOne(1L);

        var expected = cardRepository.save(createNewCard());

        Card actual = cardRepository.findCardByOwnerAndId(owner, expected.getId())
                .orElseThrow(() -> new NoSuchElementException("We can not find card with owner " + owner + " and id " + expected.getId()));

        assertEquals(expected, actual, "Here cards need to be equals");
    }

    @Test
    public void test_FindCardByNumber() {
        var expected = cardRepository.save(createNewCard());

        Card actual = cardRepository.findCardByNumber(expected.getNumber())
                .orElseThrow(() -> new NoSuchElementException("We can not find card with number: " + expected.getNumber()));

        assertEquals(expected, actual, "Here cards need to be equals");
    }

    private Card createNewCard() {
        Customer owner = customerRepository.findById(1L).orElseThrow(() ->
                new EntityNotFoundException("Customer with id" + 1 + " not found"));

        Account account = accountRepository.findById(1L).orElseThrow(() ->
                new EntityNotFoundException("Account with id" + 1 + " not found"));

        Card card = new Card();
        card.setOwner(owner);
        card.setAccount(account);

        return card;
    }
}
