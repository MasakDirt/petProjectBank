package com.pet.project.model.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Getter
@AllArgsConstructor
public class LoginRequest {

    @Pattern(regexp = "[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}", message = "Must be a valid e-mail address")
    @NotBlank
    private String username;

    @NotBlank(message = "Password cannot be blank!")
    private String password;
}
