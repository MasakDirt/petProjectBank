package com.pet.project.service.impl;

import com.pet.project.exception.NullEntityReferenceException;
import com.pet.project.model.entity.Customer;
import com.pet.project.model.entity.Role;
import com.pet.project.repository.CustomerRepository;
import com.pet.project.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Customer create(Customer customer, Role role) {
        try {
            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
            customer.setRole(role);
            return customerRepository.save(customer);
        } catch (InvalidDataAccessApiUsageException | NullPointerException exception) {
            throw new NullEntityReferenceException("Customer cannot be 'null'");
        }
    }

    @Override
    public Customer readById(long id) {
        return customerRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Customer with id " + id + " not found"));
    }

    @Override
    public void delete(long id) {
        Customer customer = readById(id);
        customerRepository.delete(customer);
    }

    @Override
    public Customer update(Customer customer, String oldPassword) {
        if (customer != null && oldPassword != null) {
            Customer oldCustomer = readById(customer.getId());
            return checkPasswords(oldPassword, oldCustomer);
        }
        throw new NullEntityReferenceException("Customer or password cannot be 'null'");
    }

    @Override
    public Customer loadUserByUsername(String email) {
        if (email != null) {
            return customerRepository.findCustomerByEmail(email).orElseThrow(() ->
                    new EntityNotFoundException("Customer with email " + email + " not found"));
        }
        throw new NullEntityReferenceException("Email cannot be 'null'");
    }

    @Override
    public List<Customer> getAll() {
        List<Customer> customers = customerRepository.findAll();
        return customers.isEmpty() ? new ArrayList<>() : customers;
    }

    private Customer checkPasswords(String oldPassword, Customer customer) {
        if (!passwordEncoder.matches(oldPassword, customer.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong old password");
        }
        return customerRepository.save(customer);
    }
}
