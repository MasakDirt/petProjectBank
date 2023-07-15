package com.pet.project.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class JwtUtilsTests {
    private final JwtUtils jwtUtils;

    @Autowired
    public JwtUtilsTests(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(jwtUtils).isNotNull();
    }

    @Test
    public void test_GenerateTokenFromUsernameAndGetSubject() {
        String expected = "example@mail.co";
        String token = jwtUtils.generateTokenFromUsername(expected);

        assertNotNull(token);

        String actual = jwtUtils.getSubject(token);
        assertEquals(expected, actual,
                "In this test must be true because we have equals token generated from username " +
                        "and getting subject from right token.");
    }

    @Test
    public void test_ValidateJwtToken_ValidToken_ReturnsTrue() {
        String token = jwtUtils.generateTokenFromUsername("example@mail.co");

        boolean result = jwtUtils.validateJwtToken(token);
        assertTrue(result,"In this test must be true because we have equals token generated from username");
    }

    @Test
    public void test_ValidateJwtToken_InvalidToken_ReturnsFalse() {
        boolean result = jwtUtils.validateJwtToken("token");
        assertFalse(result, "In this test must be false because we have token that our jwtUtils has not!");
    }

    @Test
    public void test_GetSubject_NotValid() {
        assertThrows(IllegalArgumentException.class, () -> jwtUtils.getSubject(""),
        "In this test must be IllegalArgumentException because of not right token!");
    }
}
