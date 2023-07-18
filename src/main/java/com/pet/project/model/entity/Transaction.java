package com.pet.project.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Table
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "done_at")
    @CreationTimestamp
    private LocalDateTime doneAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    @JoinColumn(name = "account_id")
    private Account account;

    @NotNull
    @DecimalMin(value = "0.1", message = "Transfer amount cannot be less than 0.1")
    private BigDecimal transferAmount;

    @NotNull
    @DecimalMin(value = "0", message = "Balance cannot be less than 0")
    private BigDecimal balanceAfter;

    @NotNull
    @DecimalMax(value = "0", message = "Withdrawals cannot be bigger than 0")
    private BigDecimal fundsWithdrawn;

    @NotNull
    @Pattern(regexp = "\\d{4} \\d{4} \\d{4} \\d{4}", message = "Invalid credit card number format")
    @JoinColumn(name = "recipient_card_number")
    private String recipientCard;

    public Transaction() {
        doneAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return id == that.id && Objects.equals(doneAt.toLocalDate(), that.doneAt.toLocalDate())
                && Objects.equals(doneAt.getHour(), that.doneAt.getHour()) && Objects.equals(account, that.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, doneAt, account);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", createdAt=" + doneAt +
                ", account=" + account +
                '}';
    }
}
