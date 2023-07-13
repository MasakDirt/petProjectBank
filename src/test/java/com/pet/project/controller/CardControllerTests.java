package com.pet.project.controller;

import com.pet.project.model.dto.auth.LoginRequest;
import com.pet.project.model.dto.card.CardBalanceUpdateRequest;
import com.pet.project.model.dto.card.CardMapper;
import com.pet.project.service.CardService;
import com.pet.project.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import static com.pet.project.controller.ControllerTestsStaticHelper.asJsonString;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class CardControllerTests {
    private static final String BASIC_URL = "/api/customers/{owner-id}/cards";
    private final MockMvc mvc;
    private final CardService cardService;
    private final CustomerService customerService;
    private final CardMapper mapper;
    private String token;

    @Autowired
    public CardControllerTests(MockMvc mvc, CardService cardService,
                               CustomerService customerService, CardMapper cardMapper) {
        this.mvc = mvc;
        this.cardService = cardService;
        this.customerService = customerService;
        this.mapper = cardMapper;
    }

    @BeforeEach
    void init() throws Exception {
        // ADMIN
        token = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new LoginRequest("mike@mail.co", "1111"))
                        )
                )
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(mvc).isNotNull();
        assertThat(cardService).isNotNull();
    }

    @Test
    public void test_Valid_GetAllOwnersCards() throws Exception {
        long ownerId = 2L;
        var expected = asJsonString(
                customerService.readById(ownerId).getMyCards()
                        .stream()
                        .map(mapper::cardToCardResponse)
                        .collect(Collectors.toList())
        );

        mvc.perform(get(BASIC_URL, ownerId)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(expected, result.getResponse().getContentAsString(),
                                "All cards reads by owner id must be equal!")
                );
    }

    @Test
    public void test_Valid_GetOwnerCard() throws Exception {
        long ownerId = 1L;
        long id = 2L;
        var expected = cardService.readByOwner(customerService.readById(ownerId), id);

        mvc.perform(get(BASIC_URL + "/{id}", ownerId, id)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(asJsonString(mapper.cardToCardResponse(expected)),
                                result.getResponse().getContentAsString(),
                                "Cards must be equal!"));
    }

    @Test
    public void test_Invalid_NotThisOwnerCard_GetOwnerCard() throws Exception {
        long ownerId = 2L;
        long id = 2L;

        mvc.perform(get(BASIC_URL + "/{id}", ownerId, id)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertNotEquals(null, result.getResponse().getContentAsString(),
                                "Here must be a response about problem that it is not his card!")
                );
    }

    @Test
    public void test_Valid_CreateCard() throws Exception {
        long ownerId = 1L;
        int cardsBeforeCreating = cardService.getAllByOwner(customerService.readById(ownerId)).size();

        mvc.perform(post(BASIC_URL, ownerId)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isCreated());

        assertTrue(cardsBeforeCreating < cardService.getAllByOwner(customerService.readById(ownerId)).size(),
                "In this method must be true, because size of all owners cards become bigger!");
    }

    @Test
    public void test_Invalid_OwnerNotFound_CreateCard() throws Exception {
        mvc.perform(post(BASIC_URL, 100L)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertNotEquals(null, result.getResponse().getContentAsString(),
                                "Here must be a response about problem that owner not found!")
                );
    }

    @Test
    public void test_Valid_UpdateBalanceCard() throws Exception {
        long ownerId = 2L;
        long id = 4L;
        double sum = 1000.25;
        var updateRequest = new CardBalanceUpdateRequest(sum);

        var expected = cardService.readByOwner(customerService.readById(ownerId), id);
        expected.getAccount().setBalance(
                expected.getAccount().getBalance().add(BigDecimal.valueOf(sum))
        );

        mvc.perform(put(BASIC_URL + "/{id}", ownerId, id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updateRequest))
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(asJsonString(mapper.cardToCardResponse(expected)), result.getResponse().getContentAsString(),
                                "Cards balance must be equals!")
                );
    }

    @Test
    public void test_Invalid_Sum_UpdateBalanceCard() throws Exception {
        long ownerId = 2L;
        long id = 4L;
        double sum = -200;
        var updateRequest = new CardBalanceUpdateRequest(sum);

        var expected = cardService.readByOwner(customerService.readById(ownerId), id);
        expected.getAccount().setBalance(
                expected.getAccount().getBalance().add(BigDecimal.valueOf(sum))
        );

        mvc.perform(put(BASIC_URL + "/{id}", ownerId, id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updateRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertNotEquals(null, result.getResponse().getContentAsString(),
                                "Here must be a response about problem that sum must be greater!")
                );
    }

    @Test
    public void test_Valid_DeleteCard() throws Exception {
        long ownerId = 3L;
        long id = 5L;
        var owner = customerService.readById(ownerId);

        int cardsBeforeDeleting = owner.getMyCards().size();

        mvc.perform(delete(BASIC_URL + "/{id}", ownerId, id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals("{\"message\":\"" + owner.getName() + " card with number " + cardService.readById(id).getNumber() + " has been deleted!\"}",
                                result.getResponse().getContentAsString(),
                                "Messages must be equal.")
                );
        int after = cardService.getAllByOwner(owner).size();

        assertNotEquals(cardsBeforeDeleting, after,
                "");
    }
}
