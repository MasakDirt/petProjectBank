package com.pet.project.model.dto.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardBalanceUpdateRequest {

    @JsonProperty
    @DecimalMin(value = "0.1", message = "Sum cannot be less than 0.1")
    private double sum;

}
