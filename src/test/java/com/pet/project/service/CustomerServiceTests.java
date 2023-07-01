package com.pet.project.service;

import com.pet.project.exception.NullEntityReferenceException;
import com.pet.project.model.entity.Customer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class CustomerServiceTests {
    private final CustomerService customerService;
    private final RoleService roleService;
    private static Customer validCustomer;
    private List<Customer> customers;

    @Autowired
    public CustomerServiceTests(CustomerService customerService, RoleService roleService) {
        this.customerService = customerService;
        this.roleService = roleService;
    }

    @BeforeAll
    static void setUp() {
        validCustomer = new Customer();
        validCustomer.setId(5L);
        validCustomer.setFirstName("Lila");
        validCustomer.setLastName("Novus");
        validCustomer.setEmail("novlila@mail.co");
        validCustomer.setPassword("JKlk67^&");
    }

    @BeforeEach
    void init() {
        customers = customerService.getAll();
    }

    @Test
    public void checkGetAllMethod() {
        assertAll(
                () -> assertTrue(customerService.getAll().size() > 0, "There will be more than 0 customers."),
                () -> assertEquals(customers.size(), customerService.getAll().size(), "Your getAll method don`t function well, check it!")
        );
    }

    @Test
    public void checkCreateUser() {
        validCustomer.setRole(roleService.readById(2L));
        customerService.create(validCustomer);

        assertTrue(customers.size() < customerService.getAll().size(), "Please, check why your service don`t create customer");
    }

    @Test
    public void checkNotValidUserCreate() {
        assertAll(
                () -> assertThrows(NullEntityReferenceException.class, () -> customerService.create(null), "There need to be NullEntityReferenceException" +
                        " because we are pass null"),
                () -> assertThrows(ConstraintViolationException.class, () -> customerService.create(new Customer()), "There need to be ConstraintViolationException" +
                        " because we are pass object without tabular values")
        );
    }

    @Test
    public void checkReadByIdUser() {
        Customer actual = customerService.readById(validCustomer.getId());

        assertEquals(validCustomer, actual, "They need to be equals, please check there`s id");
    }

    @Test
    public void checkNotValidReadByIdUser() {
        assertThrows(NoSuchElementException.class, () -> customerService.readById(200000L), "There might be NoSuchElementException " +
                "because we have not customer with id 200000");
    }

    @Test
    public void checkDeleteCustomer() {
        customerService.delete(validCustomer.getId());

        assertTrue(customers.size() > customerService.getAll().size(), "There customers collection need to be bigger than get all, because of deleting one user from db/");
    }

    @Test
    public void checkNotValidDeleteUser() {
        assertThrows(NoSuchElementException.class, () -> customerService.delete(0), "There might be NoSuchElementException " +
                "because we have not customer with id 0");
    }

    @Test
    public void checkUpdateUser() {
        Customer actual = validCustomer;
        actual.setId(2L);

        customerService.update(actual);

        assertAll(
                () -> assertEquals(customerService.readById(2L), actual),
                () -> assertEquals(customerService.getAll().size(), customers.size())
        );
    }

    @Test
    public void checkNotValidUpdate() {
        assertAll(
                () -> assertThrows(NoSuchElementException.class, () -> customerService.update(new Customer()), "There we will get NoSuchElementException because we have not customer with id 0"),
                () -> assertThrows(NullEntityReferenceException.class, () -> customerService.update(null), "There we will get NullEntityReferenceException because of null parameter")
        );
    }

    @Test
    public void checkFindByEmailUser() {
        Customer actual = customerService.findByEmail(validCustomer.getEmail());

        assertEquals(validCustomer, actual, "They need to be equals, please check there`s email`s");
    }

    @Test
    public void checkNotValidFindByEmail() {
        assertAll(
                () -> assertThrows(NoSuchElementException.class, () -> customerService.findByEmail("fakeEmailForTests@mail.co"), "There we will get NoSuchElementException" +
                        " because we have not customer in DB with that email"),
                () -> assertThrows(NullEntityReferenceException.class, () -> customerService.findByEmail(null), "There we will get NullEntityReferenceException because of null parameter")
        );
    }
}

