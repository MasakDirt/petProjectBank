package com.pet.project.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Table(name = "customer")
@Entity
@NoArgsConstructor
public class Customer {
    private static final String NAME_REGEXP = "[A-Z][a-z]+(-[A-Z][a-z]+){0,1}";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Card> myCards;


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
                '}';
    }

    public String getName() {
        return firstName + " " + lastName;
    }
}
