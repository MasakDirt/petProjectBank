package com.pet.project.service.impl;

import com.pet.project.exception.NullEntityReferenceException;
import com.pet.project.model.entity.Card;
import com.pet.project.model.entity.Customer;
import com.pet.project.repository.CardRepository;
import com.pet.project.service.CardService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class CardServiceImpl implements CardService {
   private final CardRepository cardRepository;

    @Override
    public Card create(Card card) {
        try {
            return cardRepository.save(card);
        } catch (IllegalArgumentException exception) {
            throw new NullEntityReferenceException("Card cannot be 'null'");
        }
    }

    @Override
    public void delete(long id) {
        Card card = readById(id);
        cardRepository.delete(card);
    }

    @Override
    public Card update(Card card) {
        if (card != null) {
            Card oldCard = readById(card.getId());
            if (oldCard != null) {
                return cardRepository.save(card);
            }
        }
        throw new NullEntityReferenceException("Card cannot be 'null'");
    }

    @Override
    public Card readById(long id) {
        return cardRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Card with id " + id + " not found"));
    }

    @Override
    public Card readByNumber(String number) {
        if (number != null) {
            return cardRepository.findCardByNumber(number).orElseThrow(() ->
                    new NoSuchElementException("Card with number " + number + " not found"));
        }
        throw new NullEntityReferenceException("Number cannot be 'null'");
    }

    @Override
    public Card readByOwner(Customer owner, long cardId) {
        return cardRepository.findCardByOwnerAndId(owner, cardId).orElseThrow(() ->
                new NoSuchElementException(owner.getFirstName() + " " + owner.getLastName() + " card with id: " + cardId + " not found"));
    }

    @Override
    public List<Card> getAllByOwner(Customer owner) {
        if (owner != null) {
            return owner.getCards();
        }
        throw new NullEntityReferenceException("Owner cannot be null!");
    }

    @Override
    public List<Card> getAll() {
        List<Card> cards = cardRepository.findAll();
        return cards.isEmpty() ? new ArrayList<>() : cards;
    }
}
