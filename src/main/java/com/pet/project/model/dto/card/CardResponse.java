package com.pet.project.model.dto.card;

import lombok.*;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardResponse {
    private long id;

    @NotBlank(message = "The number cannot be blank")
    private String number;

    @NonNull
    @DecimalMin(value = "0", message = "Account cannot be less than 0")
    private BigDecimal balance;
}
