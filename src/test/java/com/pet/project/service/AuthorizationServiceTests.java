package com.pet.project.service;

import com.pet.project.model.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class AuthorizationServiceTests {
    private final AuthorizationService authorizationService;
    private final CustomerService customerService;
    private final RoleService roleService;
    private Role admin;
    private Role user;

    @Autowired
    AuthorizationServiceTests(AuthorizationService authorizationService, CustomerService customerService, RoleService roleService) {
        this.authorizationService = authorizationService;
        this.customerService = customerService;
        this.roleService = roleService;
    }

    @BeforeEach
    void init() {
        user = roleService.readByName("USER");
        admin = roleService.readByName("ADMIN");
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(authorizationService).isNotNull();
        assertThat(customerService).isNotNull();
        assertThat(roleService).isNotNull();
    }

    @Test
    public void isAdminMethodTrue() {
        var customer = customerService.readById(2L);
        customer.setRole(admin);

        assertTrue(authorizationService.isAdmin(customer.getUsername()),
                "There must be true because of customers role that we set.");
    }

    @Test
    public void isAdminMethodFalse() {
        var customer = customerService.readById(2L);
        customer.setRole(user);

        assertFalse(authorizationService.isAdmin(customer.getUsername()),
                "There must be false because of customers role that we set.");
    }

    @Test
    public void isUserAdminOrIsUsersSameTrueADMIN() {
        var customer = customerService.readById(2L);
        customer.setRole(admin);

        assertTrue(authorizationService.isUserAdminOrIsUsersSame(customer.getUsername(), 1L),
                "Here, must be true because our customer is ADMIN.");
    }

    @Test
    public void isUserAdminOrIsUsersSameTrueUSER() {
        var customer = customerService.readById(2L);
        customer.setRole(user);

        assertTrue(authorizationService.isUserAdminOrIsUsersSame(customer.getUsername(), customer.getId()),
                "Here, must be true because our customer id with id which we pass on equals.");
    }

    @Test
    public void isUserAdminOrIsUsersSameFalse() {
        var customer = customerService.readById(2L);
        customer.setRole(user);

        assertFalse(authorizationService.isUserAdminOrIsUsersSame(customer.getUsername(), 0),
                "Here, must be false because customer isn`t user and his id don`t equals 0.");
    }

    @Test
    public void isUserAdminOrIsUsersSameException() {
        assertThrows(EntityNotFoundException.class, () -> authorizationService.isUserAdminOrIsUsersSame("", 0),
                "Here, must be EntityNotFoundException because we can not find user with '' email!");
    }

    @Test
    public void isUserValidUserAndIsCardOwnerTrue() {
        var customer = customerService.readById(2L);
        customer.setRole(user);

        assertTrue(authorizationService.isUserValidUserAndIsCardOwner(customer.getUsername(), customer.getId(), customer.getMyCards().get(0).getId()),
                "Here must be true because we have fully valid customer with his id and card.");
    }

    @Test
    public void isUserValidUserAndIsCardOwnerFalse() {
        var customer = customerService.readById(3L);
        customer.setRole(admin);

        assertAll(
                () -> assertFalse(authorizationService.isUserValidUserAndIsCardOwner(customer.getUsername(), 100000, customer.getMyCards().get(0).getId()),
                        "Here must be false because our customer isn`t has 100000 id."),
                () -> assertThrows(EntityNotFoundException.class,
                        () -> authorizationService.isUserValidUserAndIsCardOwner(customer.getUsername(), customer.getId(), 1000000),
                        "Here must be false because our customers card isn`t has 1000000 id.")
        );
    }

    @Test
    public void isUserAdminOrIsUsersSameForUpdateTrueADMIN() {
        var customer = customerService.readById(1L);
        customer.setRole(admin);

        assertTrue(authorizationService.isUserAdminOrIsUsersSameForUpdate(customer.getUsername(), 0, 0),
                "Here must be true because customer has role admin");
    }

    @Test
    public void isUserAdminOrIsUsersSameForUpdateTrueUSER() {
        var customer = customerService.readById(1L);
        customer.setRole(user);

        assertTrue(authorizationService.isUserAdminOrIsUsersSameForUpdate(customer.getUsername(), customer.getId(), customer.getId()),
                "Here must be true because we have fully valid customer with his id and card.");
    }

    @Test
    public void isUserAdminOrIsUsersSameForUpdateFalse() {
        var customer = customerService.readById(1L);
        customer.setRole(user);

        assertAll(
                () -> assertFalse(authorizationService.isUserAdminOrIsUsersSameForUpdate(customer.getUsername(), customer.getId(), 0),
                        "Here must be false because customer id not equal to 0."),
                () -> assertFalse(authorizationService.isUserAdminOrIsUsersSameForUpdate(customer.getUsername(), 0, customer.getId()),
                        "Here must be false because customer id not equal to 0.")
        );
    }

    @Test
    public void isUserAdminOrValidUserAndIsCardOwnerTrueADMIN() {
        var customer = customerService.readById(3L);
        customer.setRole(admin);

        assertTrue(authorizationService.isUserAdminOrValidUserAndIsCardOwner(customer.getUsername(), 0, 0),
                "Here must be true because customer has role admin");
    }

    @Test
    public void isUserAdminOrValidUserAndIsCardOwnerTrueUSER() {
        var customer = customerService.readById(3L);
        customer.setRole(user);

        assertTrue(authorizationService.isUserAdminOrValidUserAndIsCardOwner(customer.getUsername(), customer.getId(), customer.getMyCards().get(0).getId()),
                "Here must be true because we have fully valid customer with his id and card.");
    }

    @Test
    public void isUserAdminOrValidUserAndIsCardOwnerFalse() {
        var customer = customerService.readById(3L);
        customer.setRole(user);

        assertAll(
                () -> assertThrows(EntityNotFoundException.class,
                        () -> authorizationService.isUserAdminOrValidUserAndIsCardOwner(customer.getUsername(), customer.getId(), 0),
                        "Here must be false because customers card id not equal to 0."),

                () -> assertFalse(authorizationService.isUserAdminOrValidUserAndIsCardOwner(customer.getUsername(), 0, customer.getId()),
                        "Here must be false because customer id not equal to 0.")
        );
    }

    @Test
    public void isUserAdminOrValidUserAndIsCardOwnerAndIsCardContainsTransactionTrueADMIN() {
        var customer = customerService.readById(1L);
        customer.setRole(admin);

        assertTrue(authorizationService.isUserAdminOrValidUserAndIsCardOwnerAndIsCardContainsTransaction(customer.getUsername(), 0, 0, 0),
                "Here must be true because customer has role admin");
    }

    @Test
    public void isUserAdminOrValidUserAndIsCardOwnerAndIsCardContainsTransactionTrueUSER() {
        var customer = customerService.readById(1L);
        customer.setRole(user);

        assertTrue(authorizationService.isUserAdminOrValidUserAndIsCardOwnerAndIsCardContainsTransaction(customer.getUsername(), customer.getId(),
                        customer.getMyCards().get(0).getId(), customer.getMyCards().get(0).getAccount().getTransactions().get(0).getId()),
                "Here must be true because we have fully valid customer with his id, card and transaction.");
    }

    @Test
    public void isUserAdminOrValidUserAndIsCardOwnerAndIsCardContainsTransactionFalse() {
        var customer = customerService.readById(1L);
        customer.setRole(user);

        assertAll(
                () -> assertFalse(authorizationService.isUserAdminOrValidUserAndIsCardOwnerAndIsCardContainsTransaction(customer.getUsername(), 0,
                                customer.getMyCards().get(0).getId(), customer.getMyCards().get(0).getAccount().getTransactions().get(0).getId()),
                        "Here must be false because customers card id not equal to 0."),

                () -> assertThrows(EntityNotFoundException.class,
                        () -> authorizationService.isUserAdminOrValidUserAndIsCardOwnerAndIsCardContainsTransaction(customer.getUsername(), customer.getId(), 0,
                                customer.getMyCards().get(0).getAccount().getTransactions().get(0).getId()),
                        "Here must be false because card id not equal to 0."),

                () -> assertFalse(authorizationService.isUserAdminOrValidUserAndIsCardOwnerAndIsCardContainsTransaction
                                (customer.getUsername(), customer.getId(), customer.getMyCards().get(0).getId(), 0),
                        "Here must be false because transaction id not equal to 0.")
        );
    }
}
