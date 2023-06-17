package com.pet.project.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Table
@Entity
public class Transaction {

    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "transaction_sequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    private long id;
    @CreationTimestamp
    private LocalDateTime doneAt;

    public Transaction() {
        doneAt = LocalDateTime.now();
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getDoneAt() {
        return doneAt;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setDoneAt(LocalDateTime createdAt) {
        this.doneAt = createdAt;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", createdAt=" + doneAt +
                '}';
    }
}
