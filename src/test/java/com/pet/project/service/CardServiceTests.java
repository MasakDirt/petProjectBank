package com.pet.project.service;

import com.pet.project.exception.NullEntityReferenceException;
import com.pet.project.model.entity.Account;
import com.pet.project.model.entity.Card;
import com.pet.project.model.entity.Customer;
import com.pet.project.model.entity.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class CardServiceTests {
    private final CardService cardService;
    private final CustomerService customerService;
    private List<Card> cards;
    private static Card validCard;
    private static int cardId = 6;

    @Autowired
    public CardServiceTests(CardService cardService, CustomerService customerService) {
        this.cardService = cardService;
        this.customerService = customerService;
    }

    @BeforeAll
    static void setUp() {
        Account account = new Account();
        account.setId(10L);
        account.setBalance(new BigDecimal(20000));

        validCard = new Card();
        validCard.setAccount(account);
    }

    @BeforeEach
    void init() {
        cards = cardService.getAll();
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(customerService).isNotNull();
        assertThat(cardService).isNotNull();
    }

    @Test
    public void checkGetAll() {
        assertAll(
                () -> assertTrue(cardService.getAll().size() > 0,
                        "There must be more than 0 cards."),
                () -> assertEquals(cards.size(), cardService.getAll().size(),
                        "Your getAll method don`t function well, check it!")
        );
    }

    @Test
    public void checkCreateMethod() {
        Card expected = validCard;
        expected.setOwner(customerService.readById(2L));
        expected.setId(cardId++);

        cardService.create(expected);
        assertTrue(cards.size() < cardService.getAll().size(),
                "Your card have not created, because allCards size are not bigger after creating a Card");
    }

    @Test
    public void checkNotValidCreate() {
        assertAll(
                () -> assertThrows(NullEntityReferenceException.class, () -> cardService.create(null),
                        "There need to be NullEntityReferenceException because we are pass null."),

                () -> assertThrows(ConstraintViolationException.class, () -> cardService.create(new Card()),
                        "There need to be ConstraintViolationException because we are pass object without tabular values.")
        );
    }

    @Test
    public void checkReadByIdCard() {
        Card expected = validCard;
        validCard.setOwner(customerService.readById(1L));
        expected.setId(5L);
        cardService.create(expected);

        Card actual = cardService.readById(5L);
        assertEquals(expected, actual,
                "Your cards need to be equals");
    }

    @Test
    public void checkNotValidReadByIdCard() {
        assertThrows(EntityNotFoundException.class, () -> cardService.readById(200000L),
                "There might be EntityNotFoundException because we have not card with id 200000!");
    }

    @Test
    public void checkDeleteCard() {
        cardService.delete(3L);

        assertTrue(cards.size() > cardService.getAll().size(),
                "There cards collection need to be bigger than get all, because of deleting one user from db.");
    }

    @Test
    public void checkNotValidDeleteCard() {
        assertThrows(EntityNotFoundException.class, () -> cardService.delete(10000000L),
                "There might be EntityNotFoundException because we have not card with id 0.");
    }

    @Test
    public void checkUpdateCard() {
        Card expected = validCard;
        expected.setOwner(customerService.readById(3L));
        expected.setId(4L);

        cardService.update(expected);

        assertAll(
                () -> assertEquals(expected, cardService.readById(4L),
                        "Cards must be equal after update"),

                () -> assertEquals(cardService.getAll().size(), cards.size(),
                        "Cards was created or deleted, check please it. It doesn't have to be this way!")
        );
    }

    @Test
    public void checkNotValidUpdateCard() {
        checkExceptions(
                () -> cardService.update(new Card()),
                () -> cardService.update(null)
        );
    }

    @Test
    public void checkReadCardByNumber() {
        Card expected = new Card();
        expected.setId(cardId++);
        expected.setOwner(customerService.readById(1L));
        expected.setAccount(new Account());
        cardService.create(expected);

        Card actual = cardService.readByNumber(expected.getNumber());

        assertThat(expected.getNumber()).isEqualTo(actual.getNumber());
        assertThat(expected.getOwner()).isEqualTo(actual.getOwner());
        assertThat(expected.getAccount().getBalance()).isEqualTo(actual.getAccount().getBalance());
    }

    @Test
    public void checkNotValidReadCardByNumber() {
        checkExceptions(
                () -> cardService.readByNumber("6758 9087 7654 9856"),
                () -> cardService.readByNumber(null)
        );
    }

    @Test
    public void checkReadByOwnerCard() {
        Customer owner = customerService.readById(3L);

        Card expected = new Card();
        expected.setId(cardId++);
        expected.setOwner(owner);
        expected.setAccount(new Account());
        cardService.create(expected);

        long id = cardService.readByNumber(expected.getNumber()).getId();

        Card actual = cardService.readByOwner(owner, id);

        assertThat(expected.getNumber()).isEqualTo(actual.getNumber());
        assertThat(expected.getOwner()).isEqualTo(actual.getOwner());
        assertThat(expected.getAccount().getBalance()).isEqualTo(actual.getAccount().getBalance());
    }

    @Test
    public void checkInvalidReadByOwnerCard() {
        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> cardService.readByOwner(customerService.readById(2L), 10L),
                        "There we will get EntityNotFoundException because we have not customer`s card with id 10."),
                () -> assertThrows(NullEntityReferenceException.class, () -> cardService.readByOwner(null, 10L),
                        "There we will get NullEntityReferenceException because of null parameter.")
        );
    }

    @Test
    public void checkGetAllByOwnerCards() {
        Customer customer = customerService.readById(2L);
        List<Card> expected = customer.getMyCards();

        assertEquals(expected, cardService.getAllByOwner(customer),
                "Here we must simply get from owner all his cards");
    }

    @Test
    public void checkInvalidGetAllByOwnerCards() {
        assertThrows(NullEntityReferenceException.class, () -> cardService.getAllByOwner(null),
                "There we will get NullEntityReferenceException because of null parameter.");
    }

    @Test
    public void checkGetHistory() {
        Card card = cardService.readById(2L);
        List<Transaction> expected = card.getAccount().getTransactions();

        assertEquals(expected, cardService.getHistory(card.getId()),
                "Here we must simply get from card id all it`s transaction`s");
    }

    private void checkExceptions(Executable executableForNoSuch, Executable executableForNullEntity) {
        assertAll(
                () -> assertThrows(EntityNotFoundException.class, executableForNoSuch,
                        "There we will get EntityNotFoundException because we have not customer with id 0."),

                () -> assertThrows(NullEntityReferenceException.class, executableForNullEntity,
                        "There we will get NullEntityReferenceException because of null parameter.")
        );
    }
}
