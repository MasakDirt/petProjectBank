package model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Table
@Entity
public class Transaction {
    @Id
    @GeneratedValue
    private long id;
    @CreationTimestamp
    private LocalDateTime doneAt;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "owner_id")
    private Customer owner;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "customer_send_id")
    private Customer willSend;

    public Transaction() {
        doneAt = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getDoneAt() {
        return doneAt;
    }

    public Customer getOwner() {
        return owner;
    }

    public Customer getWillSend() {
        return willSend;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setDoneAt(LocalDateTime createdAt) {
        this.doneAt = createdAt;
    }

    public void setOwner(Customer owner) {
        this.owner = owner;
    }

    public void setWillSend(Customer willSend) {
        this.willSend = willSend;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", createdAt=" + doneAt +
                ", owner=" + owner +
                '}';
    }
}
