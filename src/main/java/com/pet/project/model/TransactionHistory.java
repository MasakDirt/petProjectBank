package com.pet.project.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Table
@Entity
public class TransactionHistory {
    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "transactionHistory_sequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    private long id;

    @OneToMany
    @NotNull
    private List<Transaction> transactions;
    public TransactionHistory(){}

    public long getId() {
        return id;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return "TransactionHistory{" +
                "id=" + id +
                ", transactions=" + transactions +
                '}';
    }
}
