package com.pet.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonConvertor {
    public static <T> String asJsonString(final T obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
