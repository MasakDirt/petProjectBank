package com.pet.project.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Table(name = "account")
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @DecimalMin(value = "0", message = "Account cannot be less than 0")
    private BigDecimal balance;

    @OneToOne(mappedBy = "account")
    private Card card;

    @OneToMany(mappedBy = "account")
    private List<Transaction> transactions;

    public Account() {
        balance = BigDecimal.ZERO;
    }

    public long getId() {
        return id;
    }

    public BigInteger getBalance() {
        return balance.toBigInteger();
    }

    public Card getCard() {
        return card;
    }


    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setBalance(BigDecimal account) {
        this.balance = account;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", balance=" + balance +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id == account.id && balance.toBigInteger().equals(account.balance.toBigInteger()) && Objects.equals(card, account.card);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, balance, card);
    }
}
