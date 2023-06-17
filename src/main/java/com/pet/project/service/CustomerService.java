package com.pet.project.service;

import com.pet.project.model.Customer;

import java.util.List;

public interface CustomerService {
    Customer create(Customer customer);

    void delete(long id);

    Customer update(Customer customer);

    Customer readById(long id);
    Customer findByEmail(String email);

    List<Customer> getAll();
}
