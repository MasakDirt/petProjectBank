package com.pet.project;

import com.pet.project.model.entity.*;
import com.pet.project.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
@AllArgsConstructor
@Slf4j
public class SpringBootTest implements CommandLineRunner {

    private final CustomerService customerService;
    private final CardService cardService;
    private final AccountService accountService;
    private final RoleService roleService;
    private final TransactionService transactionService;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootTest.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Running Spring Boot Application");
        Role admin = new Role();
        admin.setName("ADMIN");

        Role user = new Role();
        user.setName("USER");

        roleService.create(admin);
        log.info("Admin-role was saved in db");

        roleService.create(user);
        log.info("User-role was saved in db");

        creatingFirstUser(admin);
        creatingSecondUser(user);
        creatingThirdUser(user);
    }


    private void creatingFirstUser(Role role) {
        Customer userAdmin = createCustomer("Mike", "Nicky", "mike@mail.co", "qwQW12!@", role);

        Card firstCard = createCard(120, userAdmin);
        Account accountFirstCard = firstCard.getAccount();
        Card secondCard = createCard(2000, userAdmin);
        Account accountSecondCard = secondCard.getAccount();

        Transaction transaction1 = createTransaction(secondCard);
        Transaction transaction2 = createTransaction(secondCard);
        Transaction transaction3 = createTransaction(firstCard);
        Transaction transaction4 = createTransaction(firstCard);

        accountFirstCard.setTransactions(List.of(transaction1, transaction2));
        accountSecondCard.setTransactions(List.of(transaction3, transaction4));

        transaction1.setAccount(accountFirstCard);
        transaction2.setAccount(accountFirstCard);
        transaction3.setAccount(accountSecondCard);
        transaction4.setAccount(accountSecondCard);

        List<Card> cards = List.of(firstCard, secondCard);

        userAdmin.setCard(cards);

        firstCard.setOwner(userAdmin);
        secondCard.setOwner(userAdmin);

        saveAllInDb(userAdmin, cards, List.of(accountFirstCard, accountSecondCard), 10, transaction1, transaction2, transaction3, transaction4);
    }

    private void creatingSecondUser(Role role) {
        Customer user = createCustomer("Nick", "Miles", "nike@mail.co", "asAS34#$", role);

        Card firstCard = createCard(3000, user);
        Account accountFirstCard = firstCard.getAccount();
        Card secondCard = createCard(4000, user);
        Account accountSecondCard = secondCard.getAccount();

        Transaction transaction1 = createTransaction(firstCard);
        Transaction transaction2 = createTransaction(secondCard);
        Transaction transaction3 = createTransaction(secondCard);

        accountFirstCard.setTransactions(List.of(transaction1, transaction2));
        accountSecondCard.setTransactions(List.of(transaction3));

        transaction1.setAccount(accountFirstCard);
        transaction2.setAccount(accountFirstCard);
        transaction3.setAccount(accountSecondCard);

        List<Card> cards = List.of(firstCard, secondCard);

        user.setCard(cards);

        firstCard.setOwner(user);
        secondCard.setOwner(user);

        saveAllInDb(user, cards, List.of(accountFirstCard, accountSecondCard), 1000, transaction1, transaction2);
    }

    private void creatingThirdUser(Role role) {
        Customer user = createCustomer("Mila", "Miles", "mila@mail.co", "fgFG&*", role);

        Card card = createCard(10000, user);
        Account account = card.getAccount();

        Transaction transaction1 = createTransaction(cardService.readByOwner(customerService.findByEmail("nike@mail.co"), 4));
        Transaction transaction2 = createTransaction(cardService.readByOwner(customerService.findByEmail("nike@mail.co"), 3));
        Transaction transaction3 = createTransaction(cardService.readByOwner(customerService.findByEmail("nike@mail.co"), 4));

        account.setTransactions(List.of(transaction1, transaction2, transaction3));

        transaction1.setAccount(account);
        transaction2.setAccount(account);
        transaction3.setAccount(account);

        List<Card> cards = List.of(card);

        user.setCard(cards);

        card.setOwner(user);

        saveAllInDb(user, cards, List.of(account), 2000, transaction1, transaction2, transaction3);
    }

    private Customer createCustomer(String firstName, String lastName, String email, String password, Role role) {
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setPassword(password);
        customer.setRole(role);
        return customer;
    }

    private Card createCard(int balance, Customer owner) {
        Card card = new Card();
        card.setOwner(owner);
        Account account = new Account();
        account.setBalance(new BigDecimal(balance));
        account.setCard(card);
        card.setAccount(account);
        return card;
    }

    private Transaction createTransaction(Card card) {
        Transaction transaction = new Transaction();
        transaction.setRecipientCard(card);
        return transaction;
    }

    private void saveAllInDb(Customer customer, List<Card> cards, List<Account> accounts, int sum, Transaction... transactions) {
        customerService.create(customer);
        log.info(customer.getName() + " was saved in db");
        for (Card card : cards) {
            cardService.create(card);
        }
        log.info("Card`s was saved in db");

        for (Account account : accounts) {
            accountService.create(account);
        }
        log.info("Account`s was saved in db");

        for (Transaction transaction : transactions) {
            transactionService.create(transaction, sum);
        }
        log.info("Transactions was saved in db");
    }
}