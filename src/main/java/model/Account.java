package model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;

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

    public Account() {
    }

    public long getId() {
        return id;
    }

    public BigInteger getAccount() {
        return account.toBigInteger();
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