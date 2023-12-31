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

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
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
    void injectedComponentsAreNotNull() {
        assertThat(customerService).isNotNull();
        assertThat(roleService).isNotNull();
        assertThat(customers).isNotNull();
    }

    @Test
    public void test_GetAllMethod() {
        assertAll(
                () -> assertTrue(customerService.getAll().size() > 0,
                        "There must be more than 0 customers."),

                () -> assertEquals(customers.size(), customerService.getAll().size(),
                        "Your getAll method don`t function well, check it!")
        );
    }

    @Test
    public void test_Create_User() {
        int sizeCustomersBefore = customerService.getAll().size();
        customerService.create(validCustomer, roleService.readById(2L));

        assertTrue(sizeCustomersBefore < customerService.getAll().size(),
                "Please, check why your service don`t create customer.");
    }

    @Test
    public void test_Invalid_UserCreate() {
        assertAll(
                () -> assertThrows(NullEntityReferenceException.class, () -> customerService.create(null, roleService.readById(2L)),
                        "There need to be NullEntityReferenceException because we are pass null."),

                () -> assertThrows(IllegalArgumentException.class, () -> customerService.create(new Customer(), roleService.readById(2L)),
                        "There need to be ConstraintViolationException because we are pass object without tabular values.")
        );
    }

    @Test
    public void test_ReadByIdUser() {
        validCustomer = customerService.create(validCustomer, roleService.readById(2L));
        Customer actual = customerService.readById(validCustomer.getId());

        assertEquals(validCustomer, actual, "They need to be equals, please check there`s id.");
    }

    @Test
    public void test_Invalid_ReadByIdUser() {
        assertThrows(EntityNotFoundException.class, () -> customerService.readById(200000L),
                "There might be EntityNotFoundException because we have not customer with id 200000!");
    }

    @Test
    public void test_DeleteCustomer() {
        customerService.delete(3L);

        assertTrue(customers.size() > customerService.getAll().size(),
                "There customers collection need to be bigger than get all, because of deleting one user from db.");
    }

    @Test
    public void test_Invalid_DeleteUser() {
        assertThrows(EntityNotFoundException.class, () -> customerService.delete(0),
                "There might be EntityNotFoundException because we have not customer with id 0.");
    }

    @Test
    public void test_UpdateUser() {
        Customer actual = customerService.readById(1L);
        actual.setEmail("newEmail@mail.co");
        customerService.update(actual, "1111");

        assertAll(
                () -> assertEquals(customerService.readById(actual.getId()), actual,
                        "Update method: customers must be equals"),

                () -> assertEquals(customerService.getAll().size(), customers.size(),
                        "Customers was created or deleted, check please it. It doesn't have to be this way!")
        );
    }

    @Test
    public void test_Invalid_Update() {
        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> customerService.update(new Customer(),""),
                        "There we will get EntityNotFoundException because we have not customer with id 0 and theirs password don`t equals."),

                () -> assertThrows(NullEntityReferenceException.class, () -> customerService.update(null,null),
                        "There we will get NullEntityReferenceException because of null parameter.")
        );
    }

    @Test
    public void test_FindByEmailUser() {
        customerService.create(validCustomer, roleService.readById(2L));
        Customer actual = customerService.loadUserByUsername(validCustomer.getEmail());

        assertThat(validCustomer.getEmail()).isEqualTo(actual.getEmail());
        assertThat(validCustomer.getName()).isEqualTo(actual.getName());
        assertThat(validCustomer.getRole()).isEqualTo(actual.getRole());
    }

    @Test
    public void test_NotValid_FindByEmail() {
        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> customerService.loadUserByUsername("fakeEmailForTests@mail.co"),
                        "There we will get EntityNotFoundException because we have not customer in DB with that email"),

                () -> assertThrows(NullEntityReferenceException.class, () -> customerService.loadUserByUsername(null),
                        "There we will get NullEntityReferenceException because of null parameter")
        );
    }
}

