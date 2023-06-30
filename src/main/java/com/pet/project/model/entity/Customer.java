package com.pet.project.model.entity;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Objects;

@Table(name = "customer")
@Entity
@NoArgsConstructor
public class Customer {
    private static final String NAME_REGEXP = "[A-Z][a-z]+(-[A-Z][a-z]+){0,1}";

    @Id
    @GeneratedValue(generator = "customer-generator")
    @GenericGenerator(
            name = "customer-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "customer_sequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )
    private long id;

    @Pattern(regexp = NAME_REGEXP,
            message = "Must start with a capital letter and followed by one or more lowercase")
    @NotNull
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Pattern(regexp = NAME_REGEXP,
            message = "Must start with a capital letter and followed by one or more lowercase")
    @NotNull
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Email(message = "Write a valid e-mail address")
    @NotBlank
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password cannot be blank!")
    @Column(nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE)
    private List<Card> myCards;

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public List<Card> getCards() {
        return myCards;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setCard(List<Card> cards) {
        this.myCards = cards;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return id == customer.id && Objects.equals(email, customer.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", cards='" + myCards + '\'' +
                '}';
    }

    public String getName() {
        return firstName + " " + lastName;
    }
}
