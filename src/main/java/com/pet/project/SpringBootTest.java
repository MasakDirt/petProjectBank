package com.pet.project;

import com.pet.project.model.entity.Account;
import com.pet.project.model.entity.Card;
import com.pet.project.model.entity.Customer;
import com.pet.project.model.entity.Role;
import com.pet.project.service.AccountService;
import com.pet.project.service.CardService;
import com.pet.project.service.CustomerService;
import com.pet.project.service.RoleService;
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

    CustomerService customerService;
    CardService cardService;
    AccountService accountService;
    RoleService roleService;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootTest.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Running Spring Boot Application");

        Account account = new Account();
        account.setBalance(new BigDecimal(120));

        Card card = new Card();
        card.setAccount(account);
        List<Card> cards = List.of(card);

        account.setCard(card);

        Role admin = new Role();
        admin.setName("ADMIN");

        Role user = new Role();
        user.setName("USER");

        Customer validUser = new Customer();
        validUser.setEmail("valid@cv.ua");
        validUser.setFirstName("Valid");
        validUser.setLastName("Valid");
        validUser.setPassword("qwQW12!@");
        validUser.setCard(cards);
        validUser.setRole(admin);

        card.setOwner(validUser);
        roleService.create(admin);
        log.info("Admin-role was saved in db");

        roleService.create(user);
        log.info("User-role was saved in db");

        customerService.create(validUser);
        log.info("User was saved in db");

        cardService.create(card);
        log.info("Card was saved in db");

        accountService.create(account);
        log.info("Account was saved in db");


    }
}