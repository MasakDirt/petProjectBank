package com.pet.project.model;

import javax.persistence.*;
import java.util.Random;

@Table(name = "card")
@Entity
public class Card {
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String number;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account cardAccount;

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

    public Account getCardAccount() {
        return cardAccount;
    }

    public Customer getOwner() {
        return owner;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCardAccount(Account cardAccount) {
        this.cardAccount = cardAccount;
    }

    public void setOwner(Customer owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", cardAccount=" + cardAccount +
                ", owner=" + owner +
                '}';
    }

    private void createNumberCard() {
        int randomPart = new Random().nextInt(10000);
        int iPart = randomPart > 5000 ? randomPart / 2 : randomPart * 2;
        int yPart = iPart > 2500 ? (iPart < 7500 ? iPart + 2347 : iPart - 1589) : iPart * 2 + 259;

        number = String.format("7835 %04d %04d %04d", randomPart, iPart, yPart);
    }
}
