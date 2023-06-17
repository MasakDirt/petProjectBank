package model;

import javax.persistence.*;
import java.math.BigDecimal;

@Table
@Entity
public class Account {
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private BigDecimal account = BigDecimal.ZERO;

    @ManyToOne
    private Transaction transaction;
    @ManyToOne
    private TransactionHistory history;

    Account(){
    }

    public long getId() {
        return id;
    }

    public BigDecimal getAccount() {
        return account;
    }

    public Transaction getTransaction() {
        return transaction;
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

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
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
