package com.pet.project;

import com.pet.project.model.entity.*;
import com.pet.project.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
@AllArgsConstructor
@Slf4j
public class MyBankStarter implements CommandLineRunner {

    private final CustomerService customerService;
    private final CardService cardService;
    private final AccountService accountService;
    private final RoleService roleService;
    private final TransactionService transactionService;

    public static void main(String[] args) {
        SpringApplication.run(MyBankStarter.class, args);
    }

    @Override
    public void run(String... args) {
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
        Customer userAdmin = createCustomer("Mike", "Nicky", "mike@mail.co", "1111", role);

        Card firstCard = createCard(userAdmin);
        Account accountFirst = firstCard.getAccount();
        accountFirst = replenishBalance(accountFirst, 2000);
        Card secondCard = createCard(userAdmin);
        Account accountSecond = secondCard.getAccount();
        accountSecond = replenishBalance(accountSecond, 7000);
        firstCard = accountFirst.getCard();
        secondCard = accountSecond.getCard();

        List<Card> cards = List.of(firstCard, secondCard);

        userAdmin.setMyCards(cards);

        Transaction transaction1 = createTransaction(secondCard, accountFirst, 500);
        Transaction transaction2 = createTransaction(secondCard, accountFirst, 789.34);
        Transaction transaction3 = createTransaction(firstCard, accountSecond, 1250.890);
        Transaction transaction4 = createTransaction(firstCard, accountSecond, 900);

        accountFirst.setTransactions(List.of(transaction1, transaction2));
        accountSecond.setTransactions(List.of(transaction3, transaction4));

        accountService.update(accountFirst);
        accountService.update(accountSecond);
    }

    private void creatingSecondUser(Role role) {
        Customer user = createCustomer("Nick", "Miles", "nike@mail.co", "2222", role);

        Card firstCard = createCard(user);
        Account accountFirst = firstCard.getAccount();
        accountFirst = replenishBalance(accountFirst, 200);

        Card secondCard = createCard(user);
        Account accountSecond = secondCard.getAccount();
        accountSecond = replenishBalance(accountSecond, 400);

        List<Card> cards = List.of(firstCard, secondCard);

        user.setMyCards(cards);

        Transaction transaction5 = createTransaction(secondCard, accountFirst, 20);
        Transaction transaction6 = createTransaction(secondCard, accountFirst, 130);
        Transaction transaction7 = createTransaction(firstCard, accountSecond, 333);

        accountSecond.setTransactions(List.of(transaction7));
        accountFirst.setTransactions(List.of(transaction5, transaction6));

        accountService.update(accountFirst);
        accountService.update(accountSecond);
    }

    private void creatingThirdUser(Role role) {
        Customer user = createCustomer("Mila", "Miles", "mila@mail.co", "3333", role);

        Card card = createCard(user);
        Account account = card.getAccount();
        account = replenishBalance(account, 20000);

        List<Card> cards = List.of(card);

        user.setMyCards(cards);

        Card recepientCard = cardService.readByOwner(customerService.loadUserByUsername("nike@mail.co"), 4);

        Transaction transaction8 = createTransaction(recepientCard, account, 1234);
        Transaction transaction9 = createTransaction(recepientCard, account, 1456);
        Transaction transaction10 = createTransaction(recepientCard, account, 6789);

        account.setTransactions(List.of(transaction8, transaction9, transaction10));
        accountService.update(account);
    }

    private Customer createCustomer(String firstName, String lastName, String email, String password, Role role) {
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setPassword(password);

        log.info("Customer === {} - with email: {} === was created!", customer.getName(), customer.getUsername());
        return customerService.create(customer, role);
    }

    private Card createCard(Customer owner) {
        var card = accountService.create(new Card(), owner).getCard();

        log.info("Card === with number {} === was created.", card.getNumber());
        return card;
    }

    private Transaction createTransaction(Card recepientCard, Account account, double transferAmount) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setRecipientCard(recepientCard.getNumber());

        var transac = transactionService.create(transaction, transferAmount);
        log.info("Transaction === for card number {} === was created.", account.getCard().getNumber());
        return transac;
    }

    private Account replenishBalance(Account account, double sum) {
        var response = accountService.replenishBalance(account.getId(), sum);
        cardService.update(response.getCard());

        log.info("Card === {} balance was updated, added {} funds", response.getCard().getNumber(), sum);
        return response;
    }
}