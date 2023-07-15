package com.pet.project.model.dto.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionHistoryResponse {

    private long id;

    @DecimalMax(value = "0", message = "Withdrawals cannot be bigger than 0")
    @JsonProperty("funds_withdrawn")
    private BigDecimal fundsWithdrawn;

    @JsonProperty("done_at")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime doneAt;

    @DecimalMin(value = "0", message = "Account cannot be less than 0")
    @JsonProperty("balance_after")
    private BigDecimal balanceAfter;
}
