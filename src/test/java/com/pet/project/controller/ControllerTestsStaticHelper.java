package com.pet.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pet.project.model.dto.auth.LoginRequest;
import com.pet.project.model.dto.customer.CustomerCreateRequest;

public class ControllerTestsStaticHelper {
    public static <T> String asJsonString(final T obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static LoginRequest getLoginRequest(String username, String password) {
        return new LoginRequest(username, password);
    }

    public static CustomerCreateRequest getCustomerCreateRequest(String firstName, String lastName, String email, String password) {
        return new CustomerCreateRequest(firstName, lastName, email, password);
    }
}
