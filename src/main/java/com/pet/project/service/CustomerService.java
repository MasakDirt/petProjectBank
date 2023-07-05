package com.pet.project.service;

import com.pet.project.model.entity.Customer;
import com.pet.project.model.entity.Role;

import java.util.List;

public interface CustomerService {
    Customer create(Customer customer, Role role);

    void delete(long id);

    Customer update(Customer customer, String oldPassword);
    Customer readById(long id);
    Customer loadUserByUsername(String email);

    List<Customer> getAll();
}
