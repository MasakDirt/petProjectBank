package model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Table
@Entity
public class TransactionHistory {
    @Id
    @GeneratedValue
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
