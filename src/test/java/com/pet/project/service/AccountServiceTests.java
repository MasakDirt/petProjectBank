package com.pet.project.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
public class AccountServiceTests {
    private AccountService accountService;

    @Autowired
    AccountServiceTests(AccountService accountService) {
        this.accountService = accountService;
    }
}
