package com.pet.project.model.dto.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerUpdateRequest {
    @JsonProperty("first_name")
    @Pattern(regexp = "[A-Z][a-z]+",
            message = "Must start with a capital letter followed by one or more lowercase letters")
    private String firstName;

    @JsonProperty("last_name")
    @Pattern(regexp = "[A-Z][a-z]+",
            message = "Must start with a capital letter followed by one or more lowercase letters")
    private String lastName;

    @NotBlank(message = "Can't be empty")
    @JsonProperty("old_password")
    private String oldPassword;

    @NotBlank(message = "Can't be empty")
    @JsonProperty("new_password")
    private String newPassword;
}
