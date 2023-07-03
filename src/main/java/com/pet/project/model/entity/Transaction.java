package com.pet.project.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
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

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @DecimalMin(value = "0", message = "Account cannot be less than 0")
    private BigDecimal balanceAfter;

    @JoinColumn(name = "recipient_card_id")
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
