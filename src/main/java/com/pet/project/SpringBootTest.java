package com.pet.project;

import com.pet.project.model.Account;
import com.pet.project.model.Card;
import com.pet.project.model.Customer;
import com.pet.project.repository.AccountRepository;
import com.pet.project.repository.CardRepository;
import com.pet.project.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;

@SpringBootApplication
@AllArgsConstructor
public class SpringBootTest implements CommandLineRunner {

    CustomerRepository customerRepository;
    CardRepository cardRepository;
    AccountRepository accountRepository;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootTest.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Running Spring Boot Application");
        Account account = new Account();
        account.setAccount(new BigDecimal(120));
        Card card = new Card();
        card.setCardAccount(account);
        Customer validUser = new Customer();
        validUser.setEmail("valid@cv.ua");
        validUser.setFirstName("Valid");
        validUser.setLastName("Valid");
        validUser.setPassword("qwQW12!@");
        validUser.setCard(card);

        accountRepository.save(account);
        System.out.println("Account was saved");

        cardRepository.save(card);
        System.out.println("Card was saved");

        customerRepository.save(validUser);
        System.out.println("User was saved");

    }
}