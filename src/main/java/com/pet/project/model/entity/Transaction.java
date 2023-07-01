package com.pet.project.model.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @ManyToOne
    @JoinColumn(name = "recipient_card_id")
    private Card recipientCard;

    public Transaction() {
        doneAt = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getDoneAt() {
        return doneAt;
    }

    public Account getAccount() {
        return account;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public Card getRecipientCard() {
        return recipientCard;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setDoneAt(LocalDateTime createdAt) {
        this.doneAt = createdAt;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public void setRecipientCard(Card recipientCard) {
        this.recipientCard = recipientCard;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", createdAt=" + doneAt +
                '}';
    }
}
