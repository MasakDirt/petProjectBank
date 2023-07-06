package com.pet.project.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthorizationService {
    private final CustomerService customerService;

    public boolean checkIfUsersSame(long id) {
        return customerService.readById(id).getEmail().equals(getPrincipal());
    }

    public boolean isUserAdminOrValidUserAndIsCardOwner(long ownerId, long cardId) {
        return isUserAdminOrIsUsersSame(ownerId) && isCardOwner(ownerId, cardId);
    }

    public boolean isUserAdminOrIsUsersSame(long id) {
        return isAdmin(id) || checkIfUsersSame(id);
    }

    public boolean isUserAdminOrIsUsersSameAndUpdateIdEqual(long id, long updateId) {
        return isAdmin(id) || (checkIfUsersSame(id) && checkIfUsersSame(updateId));
    }

    private boolean isCardOwner(long ownerId, long cardId) {
        return customerService.readById(ownerId).getMyCards()
                .stream()
                .anyMatch(card -> card.getId() == cardId);
    }

    private boolean isAdmin(long id) {
        if (checkIfUsersSame(id)) {
            return customerService.readById(id).getRole().getName().equals("ADMIN");
        }
        return false;
    }

    private Object getPrincipal() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
