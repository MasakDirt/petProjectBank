package com.pet.project.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

import static com.pet.project.controller.ControllerTestsStaticHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AuthControllerTests {
    private static final String BASIC_URL = "/api/auth";

    private final MockMvc mvc;

    @Autowired
    public AuthControllerTests(MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    public void test_Valid_Login() throws Exception {
        mvc.perform(post(BASIC_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(getLoginRequest("mike@mail.co", "1111"))
                        ))
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertNotEquals(null, result.getResponse(),
                                "We should get in response token, so it`s shouldn`t be null!")
                );
    }

    @Test
    public void test_Invalid_NotFound_Login() throws Exception {
        var login = getLoginRequest("notvalid@mail.co", "1234");
        mvc.perform(post(BASIC_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(login)
                        ))
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertEquals("{\"status\":\"" + HttpStatus.NOT_FOUND.name() +
                                        "\",\"message\":\"Customer with email " + login.getUsername() + " not found\",\"path\":\"http://localhost" + result.getRequest().getRequestURI() + "\"}",

                                result.getResponse().getContentAsString().charAt(0) + result.getResponse().getContentAsString().substring(43),
                                "We should get error response with message, so it`s shouldn`t be null!")
                );
    }

    @Test
    public void test_Invalid_Unauthorized_Login() throws Exception {
        mvc.perform(post(BASIC_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(getLoginRequest("mike@mail.co", "1234"))
                        ))
                .andExpect(status().isUnauthorized())
                .andExpect(result ->
                        assertEquals("{\"status\":\"" + HttpStatus.UNAUTHORIZED.name() +
                                        "\",\"message\":\"Wrong password\",\"path\":\"http://localhost" + result.getRequest().getRequestURI() + "\"}",

                                result.getResponse().getContentAsString().charAt(0) + result.getResponse().getContentAsString().substring(43),
                                "We should get error response with message, so it`s shouldn`t be null!")
                ).andExpect(result ->
                        assertEquals(ResponseStatusException.class,
                                Objects.requireNonNull(result.getResolvedException()).getClass(),
                                "Here must be ResponseStatusException, because we post not valid password!")
                );
    }

    @Test
    public void test_Valid_CreateNewCustomer() throws Exception {
        mvc.perform(post(BASIC_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(
                                        getCustomerCreateRequest("Mikel", "Proud", "proud@mail.co", "passsword9745")
                                )
                        )
                )
                .andExpect(status().isCreated())
                .andExpect(result ->
                        assertEquals("{\"firstName\":\"Mikel\",\"lastName\":\"Proud\",\"email\":\"proud@mail.co\",\"role\":\"USER\"}",

                                result.getResponse().getContentAsString().charAt(0) + result.getResponse().getContentAsString().substring(8),
                                "If all was written as need, it`s must be equal!")
                );
    }

    @Test
    public void test_Invalid_BadRequest_CreateNewCustomer() throws Exception {
        mvc.perform(post(BASIC_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(
                                        getCustomerCreateRequest("Nike", "Mouse", "mouse.co", "mouseCat")
                                )
                        )
                )
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertEquals("{\"status\":\"" + HttpStatus.BAD_REQUEST.name() +
                                        "\",\"message\":\"Must be a valid e-mail address\",\"path\":\"http://localhost" + result.getRequest().getRequestURI() + "\"}",

                                result.getResponse().getContentAsString().charAt(0) + result.getResponse().getContentAsString().substring(43),
                                "Here must be message about exception, because we write not valid e-mail address!")
                ).andExpect(result ->
                        assertEquals(org.springframework.web.bind.MethodArgumentNotValidException.class,
                                Objects.requireNonNull(result.getResolvedException()).getClass(),
                                "Here must be MethodArgumentNotValidException, because we post not valid e-mail address")
                );
    }
}
