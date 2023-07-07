package com.pet.project.model.dto.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionReadResponse {

    private long id;

    @DecimalMax(value = "0", message = "Withdrawals cannot be bigger than 0")
    @JsonProperty("funds_withdrawn")
    private BigDecimal fundsWithdrawn;

    @CreationTimestamp
    @JsonProperty("done_at")
    private LocalDateTime doneAt;

    @DecimalMin(value = "0", message = "Account cannot be less than 0")
    @JsonProperty("balance_after")
    private BigDecimal balanceAfter;

    @NotNull
    @DecimalMin(value = "0.1", message = "Transfer amount cannot be less than 0.1")
    @JsonProperty("amount_of_transfer")
    private BigDecimal transferAmount;
}
