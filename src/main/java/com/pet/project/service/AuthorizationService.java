package com.pet.project.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthorizationService {
    private final CustomerService customerService;

    public boolean isUserAdminOrValidUserAndIsCardOwner(String username, long ownerId, long cardId) {
        return isAdmin(username) || (isUsersIdEquals(username, ownerId) && isCardOwner(username, cardId));
    }

    public boolean isUserAdminOrIsUsersSame(String username, long ownerId) {
        return isAdmin(username) || isUsersIdEquals(username, ownerId);
    }

    public boolean isAdmin(String username) {
        return customerService.loadUserByUsername(username).getRole().getName().equals("ADMIN");
    }

    private boolean isUsersIdEquals(String currentUsername, long id) {
        return customerService.loadUserByUsername(currentUsername).getId() == id;
    }

    private boolean isCardOwner(String username, long cardId) {
        return customerService.loadUserByUsername(username).getMyCards()
                .stream()
                .anyMatch(card -> card.getId() == cardId);
    }

    private Object getPrincipal() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
