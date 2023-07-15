package com.pet.project.controller;

import com.pet.project.exception.InvalidAmountException;
import com.pet.project.model.dto.auth.LoginRequest;
import com.pet.project.model.dto.transaction.TransactionCreateRequest;
import com.pet.project.model.dto.transaction.TransactionMapper;
import com.pet.project.model.entity.Card;
import com.pet.project.model.entity.Transaction;
import com.pet.project.service.CardService;
import com.pet.project.service.CustomerService;
import com.pet.project.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.pet.project.controller.ControllerTestsStaticHelper.asJsonString;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class TransactionControllerTests {
    private static final String BASIC_URL = "/api/customers/{owner-id}/cards/{card-id}/transactions";

    private final MockMvc mvc;

    private final TransactionService transactionService;

    private final CustomerService customerService;

    private final CardService cardService;

    private final TransactionMapper mapper;

    private String token;

    @Autowired
    public TransactionControllerTests(MockMvc mvc, TransactionService transactionService, CustomerService customerService,
                                      CardService cardService, TransactionMapper transactionMapper) {
        this.mvc = mvc;
        this.transactionService = transactionService;
        this.customerService = customerService;
        this.cardService = cardService;
        this.mapper = transactionMapper;
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
        assertThat(transactionService).isNotNull();
        assertThat(customerService).isNotNull();
        assertThat(cardService).isNotNull();
        assertThat(mapper).isNotNull();
    }

    @Test
    @Transactional
    public void test_Valid_GetAllTransaction() throws Exception {
        long ownerId = 2L;
        long cardId = 3L;

        var expected = asJsonString(cardService.getHistory(cardId)
                .stream()
                .map(mapper::transactionToTransactionHistoryResponse)
                .collect(Collectors.toList())
        );

        mvc.perform(get(BASIC_URL, ownerId, cardId)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(expected, result.getResponse().getContentAsString(),
                                "Lists of transactions read by cardId must be equal!")
                );
    }

    @Test
    public void test_Invalid_NotFound_GetAllTransactions() throws Exception {
        mvc.perform(get(BASIC_URL, 1L, 100L)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertNotEquals(null, result.getResponse().getContentAsString(),
                                "Here must be a response about problem that card not found")
                )
                .andExpect(result ->
                        assertEquals(EntityNotFoundException.class, Objects.requireNonNull(result.getResolvedException()).getClass(),
                                "Here must be EntityNotFoundException because we pass not valid card id!")
                );
    }

    @Test
    public void test_Valid_GetTransaction() throws Exception {
        long ownerId = 1L;
        long cardId = 2L;
        long id = 3L;

        var expected = asJsonString(
                mapper.transactionToTransactionReadResponse(transactionService.readById(id))
        );

        mvc.perform(get(BASIC_URL + "/{id}", ownerId, cardId, id)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(expected, result.getResponse().getContentAsString(),
                                "Transaction read by it`s id must be equal, check id!")
                );
    }


    @Test
    public void test_Invalid_NotFound_GetTransaction() throws Exception {
        mvc.perform(get(BASIC_URL + "/{id}", 1L, 2L, 1000L)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertNotEquals(null, result.getResponse().getContentAsString(),
                                "Here must be a response about problem that transaction with that id not found")
                )
                .andExpect(result ->
                        assertEquals(EntityNotFoundException.class, Objects.requireNonNull(result.getResolvedException()).getClass(),
                                "Here must be EntityNotFoundException because we pass not valid transaction id: 10000!")
                );
    }

    @Test
    public void test_Valid_CreateTransaction() throws Exception {
        long ownerId = 1L;
        long cardId = 2L;
        double transferAmount = 120.12;
        var recipientCard = cardService.readById(4L);
        var card = cardService.readById(cardId);

        var createRequest = new TransactionCreateRequest(recipientCard.getNumber(), transferAmount);

        Transaction transaction = createTransaction(card, recipientCard, transferAmount);
        transaction.setDoneAt(LocalDateTime.now());

        var expected = asJsonString(mapper.transactionToTransactionReadResponse(transaction));

        mvc.perform(post(BASIC_URL, ownerId, cardId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(createRequest)
                        )
                )
                .andExpect(status().isCreated())
                .andExpect(result ->
                        assertEquals(expected.charAt(0) + expected.substring(8)
                                , result.getResponse().getContentAsString().charAt(0) +
                                        result.getResponse().getContentAsString().substring(9),
                                "After creating transactions need to be equal.")
                );
    }

    @Test
    public void test_Invalid_TransferAmount_CreateTransaction() throws Exception {
        long ownerId = 1L;
        long cardId = 1L;
        double transferAmount = 0;
        var recipientCard = cardService.readById(3L);

        var createRequest = new TransactionCreateRequest(recipientCard.getNumber(), transferAmount);

        mvc.perform(post(BASIC_URL, ownerId, cardId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(createRequest)
                        )
                )
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertNotEquals(null, result.getResponse().getContentAsString(),
                                "Here must be a response about problem that sum must be greater than 0.1")
                )
                .andExpect(result ->
                        assertEquals(InvalidAmountException.class, Objects.requireNonNull(result.getResolvedException()).getClass(),
                                "Here must be InvalidAmountException because we pass not right amount of funds!")
                );
    }

    @Test
    public void test_Invalid_AccessDenied_CreateTransaction() throws Exception {
        var recipientCard = cardService.readById(1L);

        var createRequest = new TransactionCreateRequest(recipientCard.getNumber(), 100);

        mvc.perform(post(BASIC_URL, 2L, 3L)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(createRequest)
                        )
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertNotEquals(null, result.getResponse().getContentAsString(),
                                "Here must be a response about problem that for this user access denied!")
                )
                .andExpect(result ->
                        assertEquals(AccessDeniedException.class, Objects.requireNonNull(result.getResolvedException()).getClass(),
                                "Here must be AccessDeniedException because we cannot create transaction for another user")
                );
    }

    @Test
    public void test_Valid_DeleteTransaction() throws Exception {
        long ownerId = 2L;
        long cardId = 3L;
        long id = 5L;

        mvc.perform(delete(BASIC_URL + "/{id}", ownerId, cardId, id)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals("{\"message\":\"Transaction for customer " + customerService.readById(ownerId).getName()
                                        + " and his/her card " + cardService.readById(cardId).getNumber() + " has been deleted!\"}",
                                result.getResponse().getContentAsString(),
                                "Strings about deleting must be equal, please check card and owner id.")
                );
    }

    @Test
    public void test_Invalid_NotFoundTransaction_DeleteTransaction() throws Exception {
        mvc.perform(delete(BASIC_URL + "/{id}", 2L, 4L, 10000L)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertNotEquals(null, result.getResponse().getContentAsString(),
                                "Here must be a response about problem that transaction with id ... not found.")
                )
                .andExpect(result ->
                        assertEquals(EntityNotFoundException.class, Objects.requireNonNull(result.getResolvedException()).getClass(),
                                "Here must be EntityNotFoundException because we cannot find transaction with id 10000")
                );
    }

    private Transaction createTransaction(Card card, Card recipientCard, double transferAmount) {
        Transaction transaction = new Transaction();
        transaction.setAccount(card.getAccount());
        transaction.setRecipientCard(recipientCard.getNumber());
        transaction.setTransferAmount(new BigDecimal(transferAmount));

        transaction.setBalanceAfter(
                card.getAccount().getBalance().subtract(new BigDecimal(transferAmount))
        );

        recipientCard.getAccount().setBalance(
                recipientCard.getAccount().getBalance().add(new BigDecimal(transferAmount))
        );

        transaction.setFundsWithdrawn(new BigDecimal("-" + transferAmount));

        return transaction;
    }
}
