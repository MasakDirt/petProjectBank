package com.pet.project.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Random;
@Getter
@Setter
@Table(name = "card")
@Entity
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String number;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id")
    private Account account;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Customer owner;

    public Card() {
        generateCardNumber();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return id == card.id && Objects.equals(number, card.number) && Objects.equals(owner, card.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, owner);
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", card balance=" + account.getBalance() +
                ", owner=" + owner +
                '}';
    }

    private void generateCardNumber() {
        int randomFirstPart = new Random().nextInt(10000);
        int randomSecondPart = new Random().nextInt(10000);
        int randomThirdPart = new Random().nextInt(10000);

        number = String.format("7835 %04d %04d %04d", randomFirstPart, randomSecondPart, randomThirdPart);
    }
}
