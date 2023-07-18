package com.pet.project.model.dto.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionCreateRequest {

    @Pattern(regexp = "\\d{4} \\d{4} \\d{4} \\d{4}", message = "Invalid credit card number format")
    @JsonProperty("card_number")
    private String cardNumber;

    @JsonProperty("amount_of_transfer")
    @DecimalMin(value = "0.1", message = "Sum cannot be less than 0.1")
    private double transferAmount;
}
