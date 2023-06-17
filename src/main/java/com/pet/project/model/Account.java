package com.pet.project.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;

@Table(name = "account")
@Entity
public class Account {
    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
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
    private BigDecimal account;
    @ManyToOne
    private TransactionHistory history;

    public Account() {
        account = BigDecimal.ZERO;
    }

    public long getId() {
        return id;
    }

    public BigInteger getAccount() {
        return account.toBigInteger();
    }

    public TransactionHistory getHistory() {
        return history;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setAccount(BigDecimal account) {
        this.account = account;
    }

    public void setHistory(TransactionHistory history) {
        this.history = history;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", account=" + account +
                ", transaction history=" + history +
                '}';
    }
}
