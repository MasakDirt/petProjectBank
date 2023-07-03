package com.pet.project.model;

import com.pet.project.model.entity.Card;
import com.pet.project.model.entity.Customer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.pet.project.model.ValidatorForTests.getViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CustomerTests {
    private static Customer validCustomer;
    private static final List<Card> MY_CARDS = List.of(new Card());

    @BeforeAll
    static void init() {
        validCustomer = new Customer();
        validCustomer.setCard(MY_CARDS);
        validCustomer.setEmail("customer@mail.co");
        validCustomer.setFirstName("Valid");
        validCustomer.setLastName("Valid");
        validCustomer.setId(1);
        validCustomer.setPassword("pres1234");
        getViolations(validCustomer);
    }

    @Test
    public void checkValidCustomer() {
        Set<ConstraintViolation<Customer>> violations = getViolations(validCustomer);
        assertEquals(0, violations.size());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCustomerName")
    public void checkInvalidsCustomerNames(String input, String errorValue) {
        Customer wrongCustomer = new Customer();
        wrongCustomer.setFirstName(input);
        wrongCustomer.setLastName("Valid");
        wrongCustomer.setId(2);
        wrongCustomer.setCard(MY_CARDS);
        wrongCustomer.setEmail("email@mail.com");
        wrongCustomer.setPassword("1234");

        // for the lastName will be the same!
        Set<ConstraintViolation<Customer>> violations = getViolations(wrongCustomer);
        assertEquals(1, violations.size());
        assertEquals(errorValue, violations.iterator().next().getInvalidValue());
    }

    private static Stream<Arguments> provideInvalidCustomerName() {
        return Stream.of(
                Arguments.of("notvalid", "notvalid"),
                Arguments.of("notValid", "notValid"),
                Arguments.of("NotValid", "NotValid"),
                Arguments.of("", ""),
                Arguments.of(null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCustomerEmail")
    public void checkInvalidEmail(String input, String errorValue) {
        Customer wrongCustomer = new Customer();
        wrongCustomer.setFirstName("Bob");
        wrongCustomer.setLastName("Marlin");
        wrongCustomer.setId(3);
        wrongCustomer.setCard(MY_CARDS);
        wrongCustomer.setEmail(input);
        wrongCustomer.setPassword("3457");

        Set<ConstraintViolation<Customer>> violations = getViolations(wrongCustomer);
        assertEquals(1, violations.size());
        assertEquals(errorValue, violations.iterator().next().getInvalidValue());
    }

    private static Stream<Arguments> provideInvalidCustomerEmail() {
        return Stream.of(
                Arguments.of("notvalid@", "notvalid@"),
                Arguments.of("no@valid...", "no@valid..."),
                Arguments.of("no@val.", "no@val."),
                Arguments.of("", ""),
                Arguments.of(null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCustomerPassword")
    public void checkInvalidPassword(String input, String errorValue) {
        Customer wrongCustomer = new Customer();
        wrongCustomer.setFirstName("Nick");
        wrongCustomer.setLastName("Green");
        wrongCustomer.setId(4);
        wrongCustomer.setCard(MY_CARDS);
        wrongCustomer.setEmail("email@mail.com");
        wrongCustomer.setPassword(input);

        Set<ConstraintViolation<Customer>> violations = getViolations(wrongCustomer);
        assertEquals(1, violations.size());
        assertEquals(errorValue, violations.iterator().next().getInvalidValue());
    }

    private static Stream<Arguments> provideInvalidCustomerPassword() {
        return Stream.of(
                Arguments.of("", ""),
                Arguments.of(null, null)
        );
    }
}
