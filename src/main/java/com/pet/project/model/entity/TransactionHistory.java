package com.pet.project.model.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Table
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class TransactionHistory {
    @Id
    @GeneratedValue(generator = "transaction-history-generator")
    @GenericGenerator(
            name = "transaction-history-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "transactionHistory_sequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    private long id;

    @NotNull
    @OneToMany(mappedBy = "account")
    private List<Transaction> transactions;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    public long getId() {
        return id;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public Account getAccount() {
        return account;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "TransactionHistory{" +
                "id=" + id +
                ", transactions=" + transactions +
                '}';
    }
}
