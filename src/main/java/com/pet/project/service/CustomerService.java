package com.pet.project.service;

import com.pet.project.model.entity.Customer;

import java.util.List;

public interface CustomerService {
    Customer create(Customer customer);

    void delete(long id);

    Customer update(Customer customer, String newPassword);
    Customer readById(long id);
    Customer loadUserByUsername(String email);

    List<Customer> getAll();
}
