package com.pet.project.model.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.util.Random;

@Table(name = "card")
@Entity
public class Card {
    @Id
    @GeneratedValue(generator = "card-generator")
    @GenericGenerator(
            name = "card-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "card_sequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    private long id;

    @Column(nullable = false)
    private String number;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Customer owner;

    public Card() {
        createNumberCard();
    }

    public long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public Account getAccount() {
        return account;
    }

    public Customer getOwner() {
        return owner;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setAccount(Account cardAccount) {
        this.account = cardAccount;
    }

    public void setOwner(Customer owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", cardAccount=" + account +
                ", owner=" + owner +
                '}';
    }

    private void createNumberCard() {
        int randomPart = new Random().nextInt(10000);
        int firstPart = randomPart > 5000 ? randomPart / 2 : randomPart * 2;
        int secondPart = firstPart > 2500 ? (firstPart < 7500 ? firstPart + 2347 : firstPart - 1589) : firstPart * 2 + 259;

        number = String.format("7835 %04d %04d %04d", randomPart, firstPart, secondPart);
    }
}