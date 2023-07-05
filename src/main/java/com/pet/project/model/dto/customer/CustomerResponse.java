package com.pet.project.model.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponse {
    private long id;

    @NotBlank(message = "The first name cannot be blank")
    private String firstName;

    @NotBlank(message = "The last name cannot be blank")
    private String lastName;

    @Pattern(regexp = "[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}", message = "Must be a valid e-mail address")
    private String email;

    @NotNull
    private String role;
}
