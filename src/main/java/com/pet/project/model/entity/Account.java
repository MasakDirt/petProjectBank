package com.pet.project.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
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

    @OneToOne(mappedBy = "account", fetch = FetchType.EAGER)
    private Card card;

    @JsonManagedReference
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Transaction> transactions;

    public Account() {
        balance = BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", balance=" + balance +
                ", card=" + card +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id == account.id && balance.equals(account.balance) && Objects.equals(card, account.card);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, balance, card);
    }
}
