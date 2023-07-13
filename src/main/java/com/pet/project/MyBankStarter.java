package com.pet.project;

import com.pet.project.model.dto.transaction.TransactionCreateRequest;
import com.pet.project.model.entity.*;
import com.pet.project.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Slf4j
@AllArgsConstructor
@SpringBootApplication
public class MyBankStarter implements CommandLineRunner {

    private final CustomerService customerService;
    private final CardService cardService;
    private final AccountService accountService;
    private final RoleService roleService;
    private final TransactionService transactionService;

    private static void writeInPropertiesFile() {
        String username = System.getenv("username");
        String password = System.getenv("password");
        String propertiesFilePath = "src/main/resources/application.properties";

        if (username == null || password == null) {
            log.warn("You need to write your username and password in Environment Variables!");
            return;
        }

        inputAndOutputInProperties(username, password, propertiesFilePath);
    }

    private static void inputAndOutputInProperties(String username, String password, String propertiesFilePath) {
        Properties properties = new Properties();
        try {
            FileInputStream input = new FileInputStream(propertiesFilePath);
            properties.load(input);
            input.close();

            properties.setProperty("spring.datasource.username", username);
            properties.setProperty("spring.datasource.password", password);
            properties.setProperty("spring.datasource.url", "jdbc:mysql://localhost:3306/myBank");

            FileOutputStream output = new FileOutputStream(propertiesFilePath);
            properties.store(output, null);

            output.flush();
            output.close();

            log.info("Username and password was successfully written in file application.properties.");
        } catch (IOException io) {
            log.error("Error: writing in property file {}", io.getMessage());
        }
    }

    public static void main(String[] args) {
        writeInPropertiesFile();
        SpringApplication.run(MyBankStarter.class, args);
    }

    @Override
    public void run(String[] args) {
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

        List<Card> cards = List.of(firstCard, secondCard);

        userAdmin.setMyCards(cards);

        Transaction transaction1 = createTransaction(secondCard.getNumber(), accountFirst, 500);
        Transaction transaction2 = createTransaction(secondCard.getNumber(), accountFirst, 789.34);
        Transaction transaction3 = createTransaction(firstCard.getNumber(), accountSecond, 1250.890);
        Transaction transaction4 = createTransaction(firstCard.getNumber(), accountSecond, 900);

        accountFirst.setTransactions(List.of(transaction1, transaction2));
        accountSecond.setTransactions(List.of(transaction3, transaction4));
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

        Transaction transaction5 = createTransaction(secondCard.getNumber(), accountFirst, 20);
        Transaction transaction6 = createTransaction(secondCard.getNumber(), accountFirst, 130);
        Transaction transaction7 = createTransaction(firstCard.getNumber(), accountSecond, 333);

        accountSecond.setTransactions(List.of(transaction7));
        accountFirst.setTransactions(List.of(transaction5, transaction6));

    }

    private void creatingThirdUser(Role role) {
        Customer user = createCustomer("Mila", "Miles", "mila@mail.co", "3333", role);

        Card card = createCard(user);
        Account account = card.getAccount();
        account = replenishBalance(account, 20000);

        List<Card> cards = List.of(card);

        user.setMyCards(cards);

        Card recepientCard = cardService.readByOwner(customerService.loadUserByUsername("nike@mail.co"), 4);

        Transaction transaction8 = createTransaction(recepientCard.getNumber(), account, 1234);
        Transaction transaction9 = createTransaction(recepientCard.getNumber(), account, 1456);
        Transaction transaction10 = createTransaction(recepientCard.getNumber(), account, 6789);

        account.setTransactions(List.of(transaction8, transaction9, transaction10));
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

    private Transaction createTransaction(String recipientCard, Account account, double transferAmount) {
        TransactionCreateRequest request = new TransactionCreateRequest(recipientCard, transferAmount);

        var transaction = transactionService.create(request, account.getId());
        log.info("Transaction === for card number {} === was created.", account.getCard().getNumber());
        return transaction;
    }

    private Account replenishBalance(Account account, double sum) {
        var response = accountService.replenishBalance(account.getId(), sum);
        cardService.update(response.getCard());

        log.info("Card === {} balance was updated, added {} funds", response.getCard().getNumber(), sum);
        return response;
    }
}