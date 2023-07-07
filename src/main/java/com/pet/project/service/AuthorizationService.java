package com.pet.project.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthorizationService {
    private final CustomerService customerService;
    private final CardService cardService;

    public boolean isUserAdminOrValidUserAndIsCardOwner(String username, long ownerId, long cardId) {
        return isAdmin(username) || (isUsersIdEquals(username, ownerId) && isCardOwner(username, cardId));
    }

    public boolean isUserValidUserAndIsCardOwner(String username, long ownerId, long cardId) {
        return isUsersIdEquals(username, ownerId) && isCardOwner(username, cardId);
    }

    public boolean isUserAdminOrValidUserAndIsCardOwnerAndIsCardContainsTransaction(String username, long ownerId, long cardId, long transactionId) {
        return isAdmin(username) || (isUsersIdEquals(username, ownerId) && isCardOwner(username, cardId)
                && isCardContainsTransaction(cardId, transactionId));
    }

    public boolean isUserAdminOrIsUsersSame(String username, long ownerId) {
        return isAdmin(username) || isUsersIdEquals(username, ownerId);
    }

    public boolean isUserAdminOrIsUsersSameForUpdate(String username, long ownerId, long requestId) {
        return (isAdmin(username) || isUsersIdEquals(username, ownerId)) && isUsersIdEquals(ownerId, requestId);
    }

    public boolean isAdmin(String username) {
        return customerService.loadUserByUsername(username).getRole().getName().equals("ADMIN");
    }

    private boolean isUsersIdEquals(String currentUsername, long id) {
        return customerService.loadUserByUsername(currentUsername).getId() == id;
    }

    private boolean isUsersIdEquals(long userId, long requestId) {
        return userId == requestId;
    }

    private boolean isCardOwner(String username, long cardId) {
        return customerService.loadUserByUsername(username).getMyCards()
                .stream()
                .anyMatch(card -> card.getId() == cardId);
    }

    private boolean isCardContainsTransaction(long cardId, long transactionId) {
        return cardService.readById(cardId).getAccount().getTransactions()
                .stream()
                .anyMatch(transaction -> transaction.getId() == transactionId);
    }
}
