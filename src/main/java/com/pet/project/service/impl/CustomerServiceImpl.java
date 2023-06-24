package com.pet.project.service.impl;

import com.pet.project.exception.NullEntityReferenceException;
import com.pet.project.model.Customer;
import com.pet.project.repository.CustomerRepository;
import com.pet.project.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    CustomerRepository customerRepository;

    @Override
    public Customer create(Customer customer) {
        try {
            return customerRepository.save(customer);
        } catch (IllegalArgumentException exception) {
            throw new NullEntityReferenceException("Customer cannot be 'null'");
        }
    }

    @Override
    public Customer readById(long id) {
        Optional<Customer> optional = customerRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new EntityNotFoundException("Customer with id " + id + " not found");
    }

    @Override
    public void delete(long id) {
        Customer customer = readById(id);
        customerRepository.delete(customer);
    }

    @Override
    public Customer update(Customer customer) {
        if (customer != null) {
            Customer oldCustomer = readById(customer.getId());
            if (oldCustomer != null) {
                return customerRepository.save(customer);
            }
        }
        throw new NullEntityReferenceException("Customer cannot be 'null'");
    }

    @Override
    public Customer findByEmail(String email) {
        if (email != null) {
            return customerRepository.findCustomerByEmail(email);
        }
        throw new NullEntityReferenceException("Email cannot be 'null'");
    }

    @Override
    public List<Customer> getAll() {
        List<Customer> customers = customerRepository.findAll();
        return customers.isEmpty() ? new ArrayList<>() : customers;
    }
}
