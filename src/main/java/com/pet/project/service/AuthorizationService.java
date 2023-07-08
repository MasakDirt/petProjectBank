package com.pet.project.service;

import com.pet.project.model.entity.Customer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthorizationService {
    private final CustomerService customerService;
    private final CardService cardService;

    public boolean isUserAdminOrIsUsersSame(String username, long ownerId) {
        return isAdmin(username) || isUsersIdEquals(username, ownerId);
    }

    public boolean isUserValidUserAndIsCardOwner(String username, long ownerId, long cardId) {
        return isUsersIdEquals(username, ownerId) && isCardOwner(username, cardId);
    }

    public boolean isUserAdminOrIsUsersSameForUpdate(String username, long ownerId, long requestId) {
        return (isAdmin(username) || isUsersIdEquals(username, ownerId)) && isUsersIdEquals(ownerId, requestId);
    }

    public boolean isUserAdminOrValidUserAndIsCardOwner(String username, long ownerId, long cardId) {
        return isAdmin(username) || isUserValidUserAndIsCardOwner(username, ownerId, cardId);
    }

    public boolean isUserAdminOrValidUserAndIsCardOwnerAndIsCardContainsTransaction(String username, long ownerId, long cardId, long transactionId) {
        return isAdmin(username) || (isUserValidUserAndIsCardOwner(username, ownerId, cardId)
                && isCardContainsTransaction(cardId, transactionId));
    }

    public boolean isAdmin(String username) {
        return getCustomer(username).getRole().getName().equals("ADMIN");
    }

    private boolean isUsersIdEquals(String currentUsername, long id) {
        return getCustomer(currentUsername).getId() == id;
    }

    private boolean isUsersIdEquals(long userId, long requestId) {
        return userId == requestId;
    }

    private boolean isCardOwner(String username, long cardId) {
        return getCustomer(username).getMyCards()
                .stream()
                .anyMatch(card -> card.getId() == cardId);
    }

    private boolean isCardContainsTransaction(long cardId, long transactionId) {
        return cardService.getHistory(cardId)
                .stream()
                .anyMatch(transaction -> transaction.getId() == transactionId);
    }

    private Customer getCustomer(String username) {
        return customerService.loadUserByUsername(username);
    }
}
