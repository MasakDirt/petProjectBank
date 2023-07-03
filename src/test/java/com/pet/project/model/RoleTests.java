package com.pet.project.model;

import com.pet.project.model.entity.Role;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import java.util.Set;
import java.util.stream.Stream;

import static com.pet.project.model.ValidatorForTests.getViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RoleTests {
    private static Role validRole;

    @BeforeAll
    static void setUp() {
        validRole = new Role();
        validRole.setId(1L);
        validRole.setName("ValidName");
    }

    @Test
    public void checkValidRole() {
        Set<ConstraintViolation<Role>> violations = getViolations(validRole);
        assertEquals(0, violations.size());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidRoleName")
    public void checkInvalidRoleName(String name, String error) {
        Role role = new Role();
        role.setName(name);

        Set<ConstraintViolation<Role>> violations = getViolations(role);

        assertEquals(1, violations.size());
        assertEquals(error, violations.iterator().next().getInvalidValue());
    }

    private static Stream<Arguments> provideInvalidRoleName() {
        return Stream.of(
                Arguments.of("", ""),
                Arguments.of(null, null)
        );
    }
}

