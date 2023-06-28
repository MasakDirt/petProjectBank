package com.pet.project.repository;

import com.pet.project.model.entity.Card;
import com.pet.project.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    @Override
    <S extends Card> S save(S card);

    @Override
    void delete(Card card);

    Optional<Card> findCardByOwner(Customer owner);

    Card findCardByNumber(String number);

    @Override
    Optional<Card> findById(Long id);

    List<Card> findAllByOwner(Customer owner);

    @Override
    List<Card> findAll();
}
