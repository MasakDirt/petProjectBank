package com.pet.project.repository;

import com.pet.project.model.entity.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

//In Repository layer I check only my methods!
@ActiveProfiles("test")
@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
public class CustomerRepositoryTests {
    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public CustomerRepositoryTests(CustomerRepository customerRepository, RoleRepository roleRepository) {
        this.customerRepository = customerRepository;
        this.roleRepository = roleRepository;
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(customerRepository).isNotNull();
        assertThat(roleRepository).isNotNull();
    }

    @Test
    public void test_FindCustomerByEmail() {
        Customer expected = new Customer();
        expected.setId(4L);
        expected.setRole(roleRepository.findByName("USER").get());
        expected.setEmail("newemail@mail.co");
        expected.setPassword("ForRepo1234%^");
        expected.setFirstName("Maks");
        expected.setLastName("Maksow");

        customerRepository.save(expected);

        Customer actual = customerRepository.findCustomerByEmail("newemail@mail.co")
                .orElseThrow(() -> new NoSuchElementException("We can not find customer with email newemail@mail.co"));
        Assertions.assertEquals(expected, actual, "Here customers need to be equals");
    }
}
