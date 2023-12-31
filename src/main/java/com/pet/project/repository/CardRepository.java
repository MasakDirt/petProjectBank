package com.pet.project.repository;

import com.pet.project.model.entity.Card;
import com.pet.project.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findCardByOwnerAndId(Customer owner, long id);

    Optional<Card> findCardByNumber(String number);
}
