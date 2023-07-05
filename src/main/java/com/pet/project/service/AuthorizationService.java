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

    private Object getPrincipal() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
