package com.pet.project.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@Table(name = "account")
@Entity
public class Account {
    @Id
    @GeneratedValue(generator = "account-generator")
    @GenericGenerator(
            name = "account-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "account_sequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    private long id;

    @Column(nullable = false)
    @NotNull
    @DecimalMin(value = "0", message = "Account cannot be less than 0")
    private BigDecimal balance;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "transaction_history_id")
    private TransactionHistory history;

    @OneToOne(mappedBy = "account")
    private Card card;

    public Account() {
        balance = BigDecimal.ZERO;
    }

    public long getId() {
        return id;
    }

    public BigInteger getBalance() {
        return balance.toBigInteger();
    }

    public TransactionHistory getHistory() {
        return history;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setBalance(BigDecimal account) {
        this.balance = account;
    }

    public void setHistory(TransactionHistory history) {
        this.history = history;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", account=" + balance +
                ", transaction history=" + history +
                '}';
    }
}
