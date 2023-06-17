package com.pet.project.repository;

import com.pet.project.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Override
    <S extends Customer> S save(S s);

    @Override
    void delete(Customer customer);

    Customer findCustomerByEmail(String email);

    @Override
    Optional<Customer> findById(Long id);

    @Override
    List<Customer> findAll();
}
