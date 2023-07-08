package com.pet.project.controller;

import com.pet.project.model.entity.Customer;

public class ControllerStaticHelper {

    public static String getRole(Customer principal) {
        return principal.getRole().getName().toLowerCase();
    }
}
