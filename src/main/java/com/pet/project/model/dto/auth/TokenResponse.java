package com.pet.project.model.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {
    private String token;
}
